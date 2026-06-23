class ApiConstants {
  ApiConstants._();

  // AppConfig.baseUrl이 이미 '/api'를 포함하므로 여기서는 빼고 작성
  static const String login = '/users/login';
  static const String logout = '/users/logout';
  static const String refreshToken = '/users/refresh-token';
  static const String googleLogin = '/users/google-login';
  static const String kakaoLogin = '/users/kakao-login';

  static const String signup = '/users';          // POST
  static const String myInfo = '/users';           // GET
  static const String updateInfo = '/users';       // PATCH
  static const String changePassword = '/users/password'; // PATCH
  static const String withdraw = '/users';         // DELETE
}