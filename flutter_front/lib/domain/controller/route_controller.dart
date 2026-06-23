import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:geolocator/geolocator.dart';
import 'package:flutter_foreground_task/flutter_foreground_task.dart';

import 'package:flutter_front/config/app_config.dart';
import 'package:flutter_front/domain/service/route_service.dart';
import 'package:flutter_front/service/location_track_handler.dart';

/// 🗺️ 이동경로 추적 컨트롤러
/// - 추적 시작/정지 상태 관리
/// - 포그라운드 서비스 isolate가 보내오는 좌표를 받아 폴리라인 갱신
/// - 과거 세션 좌표 불러오기
class RouteController extends ChangeNotifier {
  final RouteService _service = RouteService(AppConfig.baseUrl);

  bool _isTracking = false;
  bool _isBusy = false; // 시작/정지 처리 중
  int? _sessionId;
  final List<LatLng> _points = [];
  String? _error;

  bool get isTracking => _isTracking;
  bool get isBusy => _isBusy;
  int? get sessionId => _sessionId;
  List<LatLng> get points => List.unmodifiable(_points);
  String? get error => _error;
  LatLng? get lastPoint => _points.isEmpty ? null : _points.last;

  // ───────────────── 숙소 근접 감지 ─────────────────
  // 앱이 켜져 있는 동안 숙소와의 거리를 보고, 50m 안이면 '이동경로 보기' 버튼을 활성화한다.
  static const double _enableRadius = 50; // 이 안이면 버튼 활성화(m)
  static const double _disableRadius = 100; // 이 밖으로 나가면 비활성화(히스테리시스로 깜빡임 방지)

  StreamSubscription<Position>? _monitorSub;
  LatLng? _accommodation;
  bool _isNearAccommodation = false;

  /// 숙소 50m 이내 여부 (UI 버튼 활성화 조건)
  bool get isNearAccommodation => _isNearAccommodation;

  /// task isolate → main isolate 로 들어오는 좌표 수신 콜백
  void _onReceiveTaskData(Object data) {
    if (data is Map) {
      final lat = (data['lat'] as num?)?.toDouble();
      final lng = (data['lng'] as num?)?.toDouble();
      if (lat != null && lng != null) {
        _points.add(LatLng(lat, lng));
        notifyListeners();
      }
    }
  }

  /// 추적 시작
  Future<bool> startTracking() async {
    if (_isTracking || _isBusy) return false;
    _isBusy = true;
    _error = null;
    notifyListeners();

    try {
      // 1. 위치 서비스 + 권한 확인
      if (!await _ensurePermissions()) return false;

      // 2. 백엔드에 세션 생성
      _sessionId = await _service.startSession(AppConfig.tempUserId);
      _points.clear();

      // 3. 추적 isolate에 넘길 값 저장 (dotenv가 없는 isolate를 위해 baseUrl도 전달)
      await FlutterForegroundTask.saveData(key: 'sessionId', value: _sessionId!);
      await FlutterForegroundTask.saveData(
          key: 'baseUrl', value: AppConfig.baseUrl);

      // 4. isolate 통신 포트 + 좌표 수신 콜백 등록
      FlutterForegroundTask.initCommunicationPort();
      FlutterForegroundTask.addTaskDataCallback(_onReceiveTaskData);

      // 5. 포그라운드 서비스 초기화 + 시작
      _initService();
      final result = await FlutterForegroundTask.startService(
        serviceId: 1001,
        notificationTitle: '이동경로 기록 중',
        notificationText: '백그라운드에서 위치를 기록하고 있어요.',
        callback: startLocationTrackingCallback,
      );

      if (result is ServiceRequestFailure) {
        _error = '추적 서비스 시작 실패: ${result.error}';
        FlutterForegroundTask.removeTaskDataCallback(_onReceiveTaskData);
        return false;
      }

      _isTracking = true;
      return true;
    } catch (e) {
      _error = '추적 시작 중 오류: $e';
      return false;
    } finally {
      _isBusy = false;
      notifyListeners();
    }
  }

  /// 추적 정지
  Future<void> stopTracking() async {
    if (!_isTracking || _isBusy) return;
    _isBusy = true;
    notifyListeners();

    try {
      await FlutterForegroundTask.stopService();
      FlutterForegroundTask.removeTaskDataCallback(_onReceiveTaskData);

      final id = _sessionId;
      if (id != null) {
        try {
          await _service.endSession(id);
        } catch (e) {
          debugPrint('🔴 세션 종료 기록 실패: $e');
        }
      }
      _isTracking = false;
    } finally {
      _isBusy = false;
      notifyListeners();
    }
  }

  /// 과거 세션의 좌표를 불러와 폴리라인으로 복원
  Future<void> loadSessionPoints(int sessionId) async {
    try {
      final pts = await _service.getPoints(sessionId);
      _points
        ..clear()
        ..addAll(pts.map((p) => LatLng(p.lat, p.lng)));
      _sessionId = sessionId;
      notifyListeners();
    } catch (e) {
      _error = '경로 불러오기 실패: $e';
      notifyListeners();
    }
  }

  /// 포그라운드 서비스 옵션 초기화
  void _initService() {
    FlutterForegroundTask.init(
      androidNotificationOptions: AndroidNotificationOptions(
        channelId: 'route_tracking',
        channelName: '이동경로 추적',
        channelDescription: '백그라운드에서 위치를 기록할 때 표시되는 알림입니다.',
      ),
      iosNotificationOptions: const IOSNotificationOptions(),
      foregroundTaskOptions: ForegroundTaskOptions(
        // 15초마다 onRepeatEvent → 남은 좌표 버퍼 플러시
        eventAction: ForegroundTaskEventAction.repeat(15000),
        autoRunOnBoot: false,
        allowWakeLock: true,
        allowWifiLock: false,
      ),
    );
  }

  /// 위치 서비스 활성화 + 권한 요청 플로우
  Future<bool> _ensurePermissions() async {
    if (!await Geolocator.isLocationServiceEnabled()) {
      _error = '기기의 위치 서비스(GPS)가 꺼져 있어요. 켜고 다시 시도해 주세요.';
      return false;
    }

    LocationPermission perm = await Geolocator.checkPermission();
    if (perm == LocationPermission.denied) {
      perm = await Geolocator.requestPermission();
    }
    if (perm == LocationPermission.denied ||
        perm == LocationPermission.deniedForever) {
      _error = '위치 권한이 거부되었어요. 설정에서 권한을 허용해 주세요.';
      return false;
    }

    // 알림 권한 (Android 13+) — 포그라운드 서비스 상시 알림 표시에 필요
    final np = await FlutterForegroundTask.checkNotificationPermission();
    if (np != NotificationPermission.granted) {
      await FlutterForegroundTask.requestNotificationPermission();
    }

    // 화면 꺼짐/백그라운드 추적을 위해 "항상 허용"이 권장됨.
    // whileInUse면 한 번 더 요청해 백그라운드 권한 승격을 유도(Android 11+는 설정 화면 안내).
    if (perm == LocationPermission.whileInUse) {
      await Geolocator.requestPermission();
    }
    return true;
  }

  /// 근접 감지 시작 — 숙소 좌표를 받아 위치 모니터링을 건다.
  /// (앱이 켜져 있는 동안만 동작)
  Future<void> startProximityMonitoring(LatLng accommodation) async {
    _accommodation = accommodation;
    if (_monitorSub != null) return; // 이미 모니터링 중

    // 포그라운드 감지는 '사용 중 허용'이면 충분
    if (!await Geolocator.isLocationServiceEnabled()) return;
    LocationPermission perm = await Geolocator.checkPermission();
    if (perm == LocationPermission.denied) {
      perm = await Geolocator.requestPermission();
    }
    if (perm == LocationPermission.denied ||
        perm == LocationPermission.deniedForever) {
      return;
    }

    const settings = LocationSettings(
      accuracy: LocationAccuracy.high,
      distanceFilter: 20, // 경계(50m)를 놓치지 않도록 모니터링은 촘촘히
    );
    _monitorSub = Geolocator.getPositionStream(locationSettings: settings)
        .listen(_onMonitorPosition);
    debugPrint('📡 숙소 근접 감지 시작 — 숙소 $accommodation');
  }

  void _onMonitorPosition(Position pos) {
    final acc = _accommodation;
    if (acc == null) return;

    final dist = Geolocator.distanceBetween(
      pos.latitude,
      pos.longitude,
      acc.latitude,
      acc.longitude,
    );

    // 히스테리시스: 50m 이내면 활성화, 100m 밖으로 나가면 비활성화
    bool next = _isNearAccommodation;
    if (dist <= _enableRadius) {
      next = true;
    } else if (dist > _disableRadius) {
      next = false;
    }
    if (next != _isNearAccommodation) {
      _isNearAccommodation = next;
      notifyListeners();
    }
  }

  Future<void> stopProximityMonitoring() async {
    await _monitorSub?.cancel();
    _monitorSub = null;
  }

  @override
  void dispose() {
    FlutterForegroundTask.removeTaskDataCallback(_onReceiveTaskData);
    _monitorSub?.cancel();
    super.dispose();
  }
}