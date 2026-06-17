import 'package:flutter/material.dart';
import 'package:flutter_front/common/constants/app_colors.dart';

/// 개월수 / 팀수 선택용 공통 BottomSheet 셀렉터
class BottomSheetSelector extends StatelessWidget {
  final String label;
  final int value;
  final List<int> options;
  final String Function(int) displayText;
  final ValueChanged<int> onSelected;

  const BottomSheetSelector({
    super.key,
    required this.label,
    required this.value,
    required this.options,
    required this.displayText,
    required this.onSelected,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: const TextStyle(fontSize: 12, color: AppColors.textSecondary)),
        const SizedBox(height: 6),
        GestureDetector(
          onTap: () => _showBottomSheet(context),
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 11),
            decoration: BoxDecoration(
              color: AppColors.white,
              borderRadius: BorderRadius.circular(8),
              border: Border.all(color: AppColors.primaryBorder),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  displayText(value),
                  style: const TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: AppColors.primary),
                ),
                const Icon(Icons.keyboard_arrow_down, color: AppColors.primary, size: 20),
              ],
            ),
          ),
        ),
      ],
    );
  }

  void _showBottomSheet(BuildContext context) {
    showModalBottomSheet(
      context: context,
      backgroundColor: AppColors.white,
      isScrollControlled: true, // 화면 높이 제어 허용
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (_) {
        return DraggableScrollableSheet(
          expand: false,
          initialChildSize: 0.5,  // 화면의 50%
          minChildSize: 0.3,
          maxChildSize: 0.75,     // 최대 75%까지 드래그 가능
          builder: (_, scrollController) {
            return Column(
              children: [
                // 핸들
                Container(
                  margin: const EdgeInsets.only(top: 12, bottom: 8),
                  width: 36,
                  height: 4,
                  decoration: BoxDecoration(
                    color: AppColors.border,
                    borderRadius: BorderRadius.circular(2),
                  ),
                ),
                // 제목
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
                  child: Align(
                    alignment: Alignment.centerLeft,
                    child: Text(label, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                  ),
                ),
                const Divider(height: 1, color: AppColors.border),
                // 선택 목록 (스크롤 가능)
                Expanded(
                  child: ListView.builder(
                    controller: scrollController,
                    itemCount: options.length,
                    itemBuilder: (_, index) {
                      final option = options[index];
                      final isSelected = option == value;
                      return ListTile(
                        onTap: () {
                          onSelected(option);
                          Navigator.pop(context);
                        },
                        title: Text(
                          displayText(option),
                          style: TextStyle(
                            fontSize: 15,
                            fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                            color: isSelected ? AppColors.primary : AppColors.textPrimary,
                          ),
                        ),
                        trailing: isSelected
                            ? const Icon(Icons.check, color: AppColors.primary)
                            : null,
                      );
                    },
                  ),
                ),
                const SizedBox(height: 8),
              ],
            );
          },
        );
      },
    );
  }
}
