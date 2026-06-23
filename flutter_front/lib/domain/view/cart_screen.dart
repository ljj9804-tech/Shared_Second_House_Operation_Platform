import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../controller/cart_controller.dart';
import '../dto/order_request.dart';
import '../service/order_service.dart';

class CartScreen extends StatefulWidget {
  const CartScreen({Key? key}) : super(key: key);

  @override
  State<CartScreen> createState() => _CartScreenState();
}

class _CartScreenState extends State<CartScreen> {
  final OrderService _orderService = OrderService();
  bool _isProcessing = false;

  Future<void> _handleCheckout(CartController cart) async {
    setState(() => _isProcessing = true);

    // 1. 상태 레이어 데이터들을 전송용 DTO 페이로드 구조로 변환
    final payloads = cart.items.values.map((item) {
      return CartItemPayload(
        productId: item.productId,
        quantity: item.quantity,
        price: item.price,
      );
    }).toList();

    final orderRequest = OrderRequest(
      userId: 1004, // 세컨하우스 테스트 유저 고정 ID
      deliveryAddress: '부산광역시 수영구 광안해변로 219',
      totalAmount: cart.totalAmount,
      items: payloads,
    );

    // 2. 서비스 통신부를 통하여 Spring Boot DB 테이블에 데이터 연동
    final isSuccess = await _orderService.sendOrder(orderRequest);

    setState(() => _isProcessing = false);

    if (isSuccess) {
      cart.clearCart();
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('🎉 주문이 백엔드 DB(sh_order)에 저장되었습니다.')),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('🚨 통신 실패: 백엔드 상태나 주소를 확인하세요.')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final cart = Provider.of<CartController>(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('🛒 장바구니 주문 센터'),
        backgroundColor: Colors.orange,
      ),
      body: Column(
        children: [
          Expanded(
            child: cart.items.isEmpty
                ? const Center(child: Text('장바구니에 상품이 없습니다.'))
                : ListView.builder(
              itemCount: cart.items.length,
              itemBuilder: (ctx, i) {
                final item = cart.items.values.toList()[i];
                return Card(
                  margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                  child: ListTile(
                    title: Text(item.name, style: const TextStyle(fontWeight: FontWeight.bold)),
                    subtitle: Text('${item.price}원 x ${item.quantity}'),
                    trailing: Text('${item.price * item.quantity}원', style: const TextStyle(color: Colors.orange, fontWeight: FontWeight.bold)),
                  ),
                );
              },
            ),
          ),
          Card(
            margin: const EdgeInsets.all(16),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    '총 금액: ${cart.totalAmount}원',
                    style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                  ),
                  _isProcessing
                      ? const CircularProgressIndicator()
                      : ElevatedButton(
                    onPressed: cart.items.isEmpty ? null : () => _handleCheckout(cart),
                    style: ElevatedButton.styleFrom(backgroundColor: Colors.orange),
                    child: const Text('주문하기'),
                  ),
                ],
              ),
            ),
          )
        ],
      ),
    );
  }
}