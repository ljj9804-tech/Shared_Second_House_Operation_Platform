import 'package:flutter/material.dart';
import 'package:flutter_front/common/constants/app_colors.dart';

/// Next.js globals.css 버튼 체계와 동일한 Flutter 공통 버튼
///
/// 종류:
/// - primary  : 주요 액션 (네이비) - 예약하기, 신청하기
/// - secondary: 보조 액션 (점선)   - 팀원 추가, 더보기
/// - danger   : 취소/삭제 (빨간)   - 예약취소, 팀원삭제
/// - outline  : 테두리만            - 목록으로, 취소
/// - disabled : 비활성              - 승인대기중
enum ButtonType { primary, secondary, danger, outline, disabled }

class CommonButton extends StatelessWidget {
  final String label;
  final VoidCallback? onTap;
  final ButtonType type;
  final bool fullWidth;
  final bool isLoading;
  final IconData? icon;

  const CommonButton({
    super.key,
    required this.label,
    this.onTap,
    this.type = ButtonType.primary,
    this.fullWidth = true,
    this.isLoading = false,
    this.icon,
  });

  @override
  Widget build(BuildContext context) {
    final style = _resolveStyle();

    Widget child = isLoading
        ? SizedBox(
            height: 18,
            width: 18,
            child: CircularProgressIndicator(
              strokeWidth: 2,
              color: style.textColor,
            ),
          )
        : icon != null
            ? Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(icon, size: 16, color: style.textColor),
                  const SizedBox(width: 6),
                  Text(label, style: TextStyle(color: style.textColor, fontWeight: FontWeight.w600, fontSize: 14)),
                ],
              )
            : Text(label, style: TextStyle(color: style.textColor, fontWeight: FontWeight.w600, fontSize: 14));

    final btn = GestureDetector(
      onTap: (onTap == null || type == ButtonType.disabled || isLoading) ? null : onTap,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 150),
        width: fullWidth ? double.infinity : null,
        padding: const EdgeInsets.symmetric(vertical: 13, horizontal: 20),
        decoration: BoxDecoration(
          color: style.bgColor,
          borderRadius: BorderRadius.circular(8),
          border: style.border,
        ),
        child: Center(child: child),
      ),
    );

    return btn;
  }

  _ButtonStyle _resolveStyle() {
    switch (type) {
      case ButtonType.primary:
        return _ButtonStyle(
          bgColor: onTap != null ? AppColors.primary : AppColors.disabled,
          textColor: onTap != null ? Colors.white : AppColors.disabledText,
        );
      case ButtonType.secondary:
        return _ButtonStyle(
          bgColor: AppColors.primaryLight,
          textColor: AppColors.primary,
          border: Border.all(color: AppColors.primary, style: BorderStyle.solid, width: 1),
        );
      case ButtonType.danger:
        return _ButtonStyle(
          bgColor: AppColors.dangerBg,
          textColor: AppColors.danger,
        );
      case ButtonType.outline:
        return _ButtonStyle(
          bgColor: Colors.transparent,
          textColor: AppColors.primary,
          border: Border.all(color: AppColors.primary, width: 1),
        );
      case ButtonType.disabled:
        return _ButtonStyle(
          bgColor: AppColors.disabled,
          textColor: AppColors.disabledText,
        );
    }
  }
}

class _ButtonStyle {
  final Color bgColor;
  final Color textColor;
  final Border? border;
  const _ButtonStyle({required this.bgColor, required this.textColor, this.border});
}
