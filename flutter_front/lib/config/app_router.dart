import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_front/common/constants/app_colors.dart';
import 'package:flutter_front/domain/controller/stay_accommodation_controller.dart';
import 'package:flutter_front/domain/controller/stay_reservation_controller.dart';
import 'package:flutter_front/domain/controller/chat_bot_controller.dart';
import 'package:flutter_front/domain/controller/restaurant_controller.dart';

// [화면 Import]
import 'package:flutter_front/domain/view/main_screen.dart';
import 'package:flutter_front/domain/view/stay_accommodation_list_screen.dart';
import 'package:flutter_front/domain/view/stay_accommodation_detail_screen.dart';
import 'package:flutter_front/domain/view/stay_reservation_calendar_screen.dart';
import 'package:flutter_front/domain/view/stay_my_reservation_screen.dart';
import 'package:flutter_front/domain/view/guest_chat_screen.dart';

final RouteObserver<ModalRoute> routeObserver = RouteObserver<ModalRoute>();

class AppRouter extends StatelessWidget {
  const AppRouter({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => StayAccommodationController()),
        ChangeNotifierProvider(create: (_) => StayReservationController()),
        ChangeNotifierProvider(create: (_) => ChatBotController()),
        ChangeNotifierProvider(create: (_) => RestaurantController()),
      ],
      child: MaterialApp(
        title: '세컨하우스',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          colorScheme: ColorScheme.fromSeed(seedColor: AppColors.primary),
          useMaterial3: true,
          scaffoldBackgroundColor: AppColors.background,
        ),
        navigatorObservers: [routeObserver],
        initialRoute: '/',
        routes: {
          // [홈]
          '/': (context) => const MainScreen(),

          // [숙소]
          '/accommodations': (context) => const StayAccommodationListScreen(),

          // [예약]
          '/my/reservations': (context) => const StayMyReservationScreen(),

          // [채팅] - arguments: { chatRoomId, currentUserId, currentUserName }
          '/chat': (context) {
            final args = ModalRoute.of(context)!.settings.arguments as Map<String, dynamic>;
            return GuestChatScreen(
              chatRoomId: args['chatRoomId'],
              currentUserId: args['currentUserId'],
              currentUserName: args['currentUserName'],
            );
          },
        },

        // [인자가 필요한 화면은 onGenerateRoute로 처리]
        onGenerateRoute: (settings) {
          // /accommodations/:id
          if (settings.name != null && settings.name!.startsWith('/accommodations/')) {
            final id = int.tryParse(settings.name!.split('/').last);
            if (id != null) {
              return MaterialPageRoute(builder: (_) => StayAccommodationDetailScreen(accommodationId: id));
            }
          }

          // /reservations/:accommodationId/calendar
          if (settings.name != null && settings.name!.startsWith('/reservations/')) {
            final args = settings.arguments as Map<String, dynamic>?;
            if (args != null) {
              return MaterialPageRoute(
                builder: (_) => StayReservationCalendarScreen(
                  accommodationId: args['accommodationId'],
                  accommodationName: args['accommodationName'],
                ),
              );
            }
          }

          return null;
        },
      ),
    );
  }
}
