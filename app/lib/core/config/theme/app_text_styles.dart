import 'package:flutter/material.dart';

abstract class AppTextStyles {
  static const String _baseFont = 'YourFontFamily'; // replace with your font

  static const TextStyle displayLarge = TextStyle(
    fontFamily: _baseFont, fontSize: 32, fontWeight: FontWeight.w700, height: 1.2,
  );
  static const TextStyle displayMedium = TextStyle(
    fontFamily: _baseFont, fontSize: 26, fontWeight: FontWeight.w700, height: 1.2,
  );
  static const TextStyle titleLarge = TextStyle(
    fontFamily: _baseFont, fontSize: 20, fontWeight: FontWeight.w600, height: 1.3,
  );
  static const TextStyle titleMedium = TextStyle(
    fontFamily: _baseFont, fontSize: 16, fontWeight: FontWeight.w600, height: 1.4,
  );
  static const TextStyle bodyLarge = TextStyle(
    fontFamily: _baseFont, fontSize: 16, fontWeight: FontWeight.w400, height: 1.5,
  );
  static const TextStyle bodyMedium = TextStyle(
    fontFamily: _baseFont, fontSize: 14, fontWeight: FontWeight.w400, height: 1.5,
  );
  static const TextStyle bodySmall = TextStyle(
    fontFamily: _baseFont, fontSize: 12, fontWeight: FontWeight.w400, height: 1.5,
  );
  static const TextStyle labelMedium = TextStyle(
    fontFamily: _baseFont, fontSize: 12, fontWeight: FontWeight.w500, height: 1.4, letterSpacing: 0.5,
  );
}