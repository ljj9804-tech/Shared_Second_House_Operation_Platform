// lib/main.dart
import 'package:flutter/material.dart';
import 'domain/service/delivery_service.dart'; // 방금 만든 서비스 임포트

// void main() {
//   runApp(const MyApp());
// }
//
// class MyApp extends StatelessWidget {
//   const MyApp({super.key});
//
//   // This widget is the root of your application.
//   @override
//   Widget build(BuildContext context) {
//     return MaterialApp(
//       title: 'Flutter Demo',
//       theme: ThemeData(
//         // This is the theme of your application.
//         //
//         // TRY THIS: Try running your application with "flutter run". You'll see
//         // the application has a purple toolbar. Then, without quitting the app,
//         // try changing the seedColor in the colorScheme below to Colors.green
//         // and then invoke "hot reload" (save your changes or press the "hot
//         // reload" button in a Flutter-supported IDE, or press "r" if you used
//         // the command line to start the app).
//         //
//         // Notice that the counter didn't reset back to zero; the application
//         // state is not lost during the reload. To reset the state, use hot
//         // restart instead.
//         //
//         // This works for code too, not just values: Most code changes can be
//         // tested with just a hot reload.
//         colorScheme: .fromSeed(seedColor: Colors.deepPurple),
//       ),
//       home: const MyHomePage(title: 'Flutter Demo Home Page'),
//     );
//   }
// }
//
// class MyHomePage extends StatefulWidget {
//   const MyHomePage({super.key, required this.title});
//
//   // This widget is the home page of your application. It is stateful, meaning
//   // that it has a State object (defined below) that contains fields that affect
//   // how it looks.
//
//   // This class is the configuration for the state. It holds the values (in this
//   // case the title) provided by the parent (in this case the App widget) and
//   // used by the build method of the State. Fields in a Widget subclass are
//   // always marked "final".
//
//   final String title;
//
//   @override
//   State<MyHomePage> createState() => _MyHomePageState();
// }
//
// class _MyHomePageState extends State<MyHomePage> {
//   int _counter = 0;
//
//   void _incrementCounter() {
//     setState(() {
//       // This call to setState tells the Flutter framework that something has
//       // changed in this State, which causes it to rerun the build method below
//       // so that the display can reflect the updated values. If we changed
//       // _counter without calling setState(), then the build method would not be
//       // called again, and so nothing would appear to happen.
//       _counter++;
//     });
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     // This method is rerun every time setState is called, for instance as done
//     // by the _incrementCounter method above.
//     //
//     // The Flutter framework has been optimized to make rerunning build methods
//     // fast, so that you can just rebuild anything that needs updating rather
//     // than having to individually change instances of widgets.
//     return Scaffold(
//       appBar: AppBar(
//         // TRY THIS: Try changing the color here to a specific color (to
//         // Colors.amber, perhaps?) and trigger a hot reload to see the AppBar
//         // change color while the other colors stay the same.
//         backgroundColor: Theme.of(context).colorScheme.inversePrimary,
//         // Here we take the value from the MyHomePage object that was created by
//         // the App.build method, and use it to set our appbar title.
//         title: Text(widget.title),
//       ),
//       body: Center(
//         // Center is a layout widget. It takes a single child and positions it
//         // in the middle of the parent.
//         child: Column(
//           // Column is also a layout widget. It takes a list of children and
//           // arranges them vertically. By default, it sizes itself to fit its
//           // children horizontally, and tries to be as tall as its parent.
//           //
//           // Column has various properties to control how it sizes itself and
//           // how it positions its children. Here we use mainAxisAlignment to
//           // center the children vertically; the main axis here is the vertical
//           // axis because Columns are vertical (the cross axis would be
//           // horizontal).
//           //
//           // TRY THIS: Invoke "debug painting" (choose the "Toggle Debug Paint"
//           // action in the IDE, or press "p" in the console), to see the
//           // wireframe for each widget.
//           mainAxisAlignment: .center,
//           children: [
//             const Text('You have pushed the button this many times:'),
//             Text(
//               '$_counter',
//               style: Theme.of(context).textTheme.headlineMedium,
//             ),
//           ],
//         ),
//       ),
//       floatingActionButton: FloatingActionButton(
//         onPressed: _incrementCounter,
//         tooltip: 'Increment',
//         child: const Icon(Icons.add),
//       ),
//     );
//   }
// }

// 테스트용 임시 main ======================================
import 'package:provider/provider.dart';
import 'domain/view/guest_chat_main_screen.dart';
import 'domain/controller/chat_bot_controller.dart'; // 🤖 챗봇 상태 컨트롤러
import 'domain/controller/restaurant_controller.dart'; // 🍽️ 맛집 상태 컨트롤러

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    seonggyu
    return MaterialApp(
      title: '배달 프로젝트 리더 전용 앱',
      theme: ThemeData(primarySwatch: Colors.blue),
      home: const DeliveryTestScreen(),
    );
  }
}

class DeliveryTestScreen extends StatefulWidget {
  const DeliveryTestScreen({super.key});

  @override
  State<DeliveryTestScreen> createState() => _DeliveryTestScreenState();
}

class _DeliveryTestScreenState extends State<DeliveryTestScreen> {
  final DeliveryService _deliveryService = DeliveryService();
  String _currentStatus = "주문 대기 중";
  bool _isLoading = false;

  // 버튼 누를 때 실행할 비동기 핸들러 함수
  void _changeStatus(int orderId, String targetStatus) async {
    setState(() {
      _isLoading = true;
    });

    // 서버로 배송 상태 변경 요청 전송!
    bool success = await _deliveryService.updateDeliveryStatus(orderId, targetStatus);

    setState(() {
      _isLoading = false;
      if (success) {
        _currentStatus = targetStatus;
      } else {
        _currentStatus = "통신 실패 ❌ (도커나 서버를 확인하세요!)";
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("🚚 배달 주문 상태 제어판"),
        centerTitle: true,
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Text(
                "현재 [6번 주문] 배달 상태",
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 10),

              // 현재 상태 표시 텍스트
              Text(
                _currentStatus,
                style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                    color: _currentStatus == "배송중" ? Colors.orange : Colors.green
                ),
              ),
              const SizedBox(height: 40),

              if (_isLoading) const CircularProgressIndicator(),

              if (!_isLoading) ...[
                // 1. 배송중으로 변경하는 버튼
                SizedBox(
                  width: double.infinity,
                  height: 55,
                  child: ElevatedButton.icon(
                    style: ElevatedButton.styleFrom(backgroundColor: Colors.orange),
                    onPressed: () => _changeStatus(6, "배송중"),
                    icon: const Icon(Icons.delivery_dining, color: Colors.white),
                    label: const Text("배송 시작하기 (배송중)", style: TextStyle(color: Colors.white, fontSize: 16)),
                  ),
                ),
                const SizedBox(height: 15),

                // 2. 배송완료로 변경하는 버튼
                SizedBox(
                  width: double.infinity,
                  height: 55,
                  child: ElevatedButton.icon(
                    style: ElevatedButton.styleFrom(backgroundColor: Colors.green),
                    onPressed: () => _changeStatus(6, "배송완료"),
                    icon: const Icon(Icons.check_circle, color: Colors.white),
                    label: const Text("배송 완료 처리", style: TextStyle(color: Colors.white, fontSize: 16)),
                  ),
                ),
              ]
            ],
          ),
        ),
      ),
    );
  }
}
=======
    // 🌐 앱 전역에서 사용할 Provider들을 최상단에 등록한다.
    // 이렇게 하면 어느 화면에서든 별도 주입 없이 컨트롤러를 구독할 수 있다.
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => ChatBotController()),
        ChangeNotifierProvider(create: (_) => RestaurantController()),
      ],
      child: MaterialApp(
        title: 'Shared Second House',
        debugShowCheckedModeBanner: false, // 디버그 마크 숨기기
        theme: ThemeData(
          colorScheme: ColorScheme.fromSeed(seedColor: Colors.blueAccent),
          useMaterial3: true,
        ),
        // ⭐ 여기를 방금 만든 메인 테스트 화면으로 지정합니다!
        home: const GuestChatMainScreen(),
      ),
    );
  }
}
// 테스트용 임시 main ======================================
middle
