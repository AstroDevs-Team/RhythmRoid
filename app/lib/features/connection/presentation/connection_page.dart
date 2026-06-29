import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:rhythmroid/core/config/router/app_router.dart';
import 'package:rhythmroid/core/config/theme/app_colors.dart';
import 'package:go_router/go_router.dart';

class ConnectionPage extends StatelessWidget {
  const ConnectionPage({super.key});

  @override
  Widget build(BuildContext context) {
    GlobalKey<FormState> formKey = GlobalKey<FormState>();
    final text = Theme.of(context).textTheme;
    return Scaffold(
      body: SafeArea(
        child: SizedBox(
          width: 1.sw,
          child: Column(
            crossAxisAlignment: .center,
            children: [
              Spacer(),
              Image.asset('assets/images/logo.png', height: 150.h),
              SizedBox(height: 20.h),
              Text(
                'Rhythmroid'.toUpperCase(),
                style: text.displayLarge!.copyWith(color: AppColors.accent),
              ), SizedBox(height: 10.h),
              Text(
                'Sync your rhythm seamlessly',
                style: text.titleSmall!.copyWith(color: AppColors.grey200),
              ),
              SizedBox(height: 50.h),
              Form(
                key: formKey,
                child: Padding(
                  padding: EdgeInsets.symmetric(horizontal: 22.w),
                  child: Column(
                    children: [
                      TextFormField(
                        decoration: const InputDecoration(
                          hintText: "192.0.0.1",
                          labelText: "IP",
                        ),
                      ),
                      SizedBox(height: 20.h),
                      ElevatedButton(
                        onPressed: () {
                          context.push(AppRoutes.playerPage);
                        },
                        child: Text('Connect'),
                      ),
                    ],
                  ),
                ),
              ),
              Spacer(flex: 4),
            ],
          ),
        ),
      ),
    );
  }
}
