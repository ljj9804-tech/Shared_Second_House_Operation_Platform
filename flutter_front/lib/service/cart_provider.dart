import 'package:flutter/material.dart';

class CartProvider with ChangeNotifier {
  final List<Map<String, dynamic>> _cartItems = [];
  final List<Map<String, dynamic>> _adminOrders = [];

  List<Map<String, dynamic>> get cartItems => _cartItems;
  List<Map<String, dynamic>> get adminOrders => _adminOrders;

  void addToCart(Map<String, dynamic> product) {
    int index = _cartItems.indexWhere((item) => item['productId'] == product['id']);
    if (index >= 0) {
      _cartItems[index]['quantity'] += 1;
    } else {
      _cartItems.add({
        "productId": product['id'],
        "name": product['name'],
        "price": product['price'],
        "quantity": 1,
        "img": product['img']
      });
    }
    notifyListeners();
  }

  void removeFromCart(int productId) {
    _cartItems.removeWhere((item) => item['productId'] == productId);
    notifyListeners();
  }

  void incrementQuantity(int productId) {
    int index = _cartItems.indexWhere((item) => item['productId'] == productId);
    if (index >= 0) {
      _cartItems[index]['quantity'] += 1;
      notifyListeners();
    }
  }

  void decrementQuantity(int productId) {
    int index = _cartItems.indexWhere((item) => item['productId'] == productId);
    if (index >= 0 && _cartItems[index]['quantity'] > 1) {
      _cartItems[index]['quantity'] -= 1;
      notifyListeners();
    }
  }

  void clearCart() {
    _cartItems.clear();
    notifyListeners();
  }

  void addOrderFromCart(int totalAmount) {
    String newOrderId = "2024-${DateTime.now().millisecondsSinceEpoch.toString().substring(9)}";
    _adminOrders.add({
      "orderId": newOrderId,
      "status": "배송 준비 중",
      "totalAmount": "$totalAmount원",
    });
    notifyListeners();
  }
}