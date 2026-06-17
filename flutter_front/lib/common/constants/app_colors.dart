import 'package:flutter/material.dart';

class AppColors {
  AppColors._();

  // 메인 색상 (Next.js --color-primary)
  static const Color primary      = Color(0xFF245B10); // 짙은 초록
  static const Color primaryHover = Color(0xFF1A4509);
  static const Color primaryLight = Color(0xFFF0F7EC);

  // 배경 (Next.js --color-background / --color-card-bg)
  static const Color background   = Color(0xFFFFFFFF);
  static const Color cardBg       = Color(0xFFFAFAFA);
  static const Color white        = Colors.white;

  // 상태
  static const Color success      = Color(0xFF00A878);
  static const Color danger       = Color(0xFFDC2626); // Next.js --color-danger
  static const Color dangerBg     = Color(0xFFFEE2E2); // Next.js --color-danger-bg
  static const Color dangerHover  = Color(0xFFFECACA); // Next.js --color-danger-hover
  static const Color disabled     = Color(0xFFE5E5E5); // Next.js --color-border
  static const Color disabledText = Color(0xFF888888); // Next.js --color-text-muted

  // 텍스트 (Next.js --color-foreground / --color-text-muted)
  static const Color textPrimary   = Color(0xFF171717);
  static const Color textSecondary = Color(0xFF888888);
  static const Color textHint      = Color(0xFFB0B7C3);

  // 카드 / 구분선 (Next.js --color-border)
  static const Color border       = Color(0xFFE5E5E5);

  // primary 투명도 변형
  static Color primaryBorder = primary.withValues(alpha: 0.20);
  static Color primaryBg     = primary.withValues(alpha: 0.04);
}
