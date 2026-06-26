import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_front/service/cart_provider.dart';

class CartScreen extends StatelessWidget {
  const CartScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
          title: const Text('장바구니', style: TextStyle(color: Colors.white)),
          backgroundColor: const Color(0xFF2E6F40)
      ),
      body: Consumer<CartProvider>(
        builder: (context, cartProvider, child) {
          final cartItems = cartProvider.cartItems;

          // Null Safety 처리: 혹시 모를 데이터 누락에 대비
          int totalAmount = cartItems.fold<int>(0, (sum, item) {
            int price = (item['price'] as num?)?.toInt() ?? 0;
            int qty = (item['quantity'] as num?)?.toInt() ?? 0;
            return sum + (price * qty);
          });

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
                                  Text(item['name'] ?? '상품명 없음', style: const TextStyle(fontWeight: FontWeight.bold)),
                                  Text('${item['price'] ?? 0}원'),
                                ],
                              ),
                            ),
                            IconButton(
                                icon: const Icon(Icons.remove),
                                onPressed: () => cartProvider.incrementQuantity(item['productId'])
                            ),
                            Text('${item['quantity'] ?? 0}'),
                            IconButton(
                                icon: const Icon(Icons.add),
                                onPressed: () => cartProvider.incrementQuantity(item['productId'])
                            ),
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
                child: Text('총 결제 금액: $totalAmount원',
                    style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
              ),
              Padding(
                padding: const EdgeInsets.only(bottom: 20.0),
                child: ElevatedButton(
                  style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF2E6F40)),
                  onPressed: () {
                    cartProvider.addOrderFromCart(totalAmount);
                    cartProvider.clearCart();

                    // 사용자 피드백 추가
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('주문이 접수되었습니다!'), duration: Duration(seconds: 2)),
                    );
                  },
                  child: const Text('주문하기', style: TextStyle(color: Colors.white)),
                ),
              )
            ],
          );
        },
      ),
    );
  }
}