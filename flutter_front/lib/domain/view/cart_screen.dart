import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_front/service/cart_provider.dart';

class CartScreen extends StatelessWidget {
  const CartScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('장바구니'), backgroundColor: const Color(0xFF2E6F40)),
      body: Consumer<CartProvider>(
        builder: (context, cartProvider, child) {
          final cartItems = cartProvider.cartItems;

          // 💡 타입 불일치 에러 해결: .toInt() 추가
          int totalAmount = cartItems.fold<int>(0, (sum, item) => sum + (item['price'] as int) * (item['quantity'] as int));

          if (cartItems.isEmpty) {
            return const Center(child: Text('장바구니가 비어 있습니다.'));
          }

          return Column(
            children: [
              Expanded(
                child: ListView.builder(
                  itemCount: cartItems.length,
                  itemBuilder: (context, index) {
                    final item = cartItems[index];
                    // 💡 레이아웃 깨짐 해결: ListTile 대신 Row 사용
                    return Card(
                      margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Row(
                          children: [
                            Expanded(
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(item['name'], style: const TextStyle(fontWeight: FontWeight.bold)),
                                  Text('${item['price']}원'),
                                ],
                              ),
                            ),
                            IconButton(icon: const Icon(Icons.remove), onPressed: () => cartProvider.decrementQuantity(item['productId'])),
                            Text('${item['quantity']}'),
                            IconButton(icon: const Icon(Icons.add), onPressed: () => cartProvider.incrementQuantity(item['productId'])),
                            IconButton(
                              icon: const Icon(Icons.delete, color: Colors.red),
                              onPressed: () => cartProvider.removeFromCart(item['productId']),
                            ),
                          ],
                        ),
                      ),
                    );
                  },
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(20.0),
                child: Text('총 결제 금액: $totalAmount원', style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
              ),
              ElevatedButton(
                onPressed: () {
                  cartProvider.addOrderFromCart(totalAmount);
                  cartProvider.clearCart();
                },
                child: const Text('주문하기'),
              )
            ],
          );
        },
      ),
    );
  }
}