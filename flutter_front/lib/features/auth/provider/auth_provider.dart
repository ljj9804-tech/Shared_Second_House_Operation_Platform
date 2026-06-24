import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:dio/dio.dart';
import '../../../core/api/dio_client.dart';
import '../../../core/api/api_constants.dart';
import '../../../core/storage/secure_storage.dart';

enum AuthStatus { unknown, authenticated, unauthenticated }

class AuthProvider extends ChangeNotifier {
  AuthStatus status = AuthStatus.unknown;
  bool isLoading = false;
  String? errorMessage;

  Future<void> checkAuthStatus() async {
    final hasToken = await SecureStorage.instance.hasAccessToken();
    status = hasToken ? AuthStatus.authenticated : AuthStatus.unauthenticated;
    notifyListeners();
  }

  Future<bool> login(String username, String password) async {
    isLoading = true;
    errorMessage = null;
    notifyListeners();

    try {
      final response = await DioClient.instance.dio.post(
        ApiConstants.login,
        data: {'username': username, 'password': password},
      );

      final body = response.data is String
          ? jsonDecode(response.data as String)
          : response.data;
      final accessToken = body['accessToken'] as String;
      await SecureStorage.instance.saveAccessToken(accessToken);

      status = AuthStatus.authenticated;
      return true;
    } on DioException catch (e) {
      errorMessage = _parseError(e);
      return false;
    } finally {
      isLoading = false;
      notifyListeners();
    }
  }

  String _parseError(DioException e) {
    final data = e.response?.data;
    if (e.response?.statusCode == 401) {
      return '아이디 또는 비밀번호가 올바르지 않습니다.';
    }
    if (data is Map && data['message'] != null) {
      return data['message'].toString();
    }
    return '로그인 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
  }

  Future<void> logout() async {
    try {
      await DioClient.instance.dio.post(ApiConstants.logout);
    } catch (_) {
      // 서버 호출이 실패해도 로컬 로그아웃은 진행
    } finally {
      await SecureStorage.instance.clearAccessToken();
      status = AuthStatus.unauthenticated;
      notifyListeners();
    }
  }
}