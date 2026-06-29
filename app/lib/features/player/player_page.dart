import 'package:audio_video_progress_bar/audio_video_progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:rhythmroid/core/config/theme/app_colors.dart';

class PlayerPage extends StatefulWidget {
  const PlayerPage({super.key});

  @override
  State<PlayerPage> createState() => _PlayerPageState();
}

class _PlayerPageState extends State<PlayerPage> {
  bool isPlaying = false;
  @override
  Widget build(BuildContext context) {
    final text = Theme.of(context).textTheme;
    return Scaffold(
      appBar: AppBar(automaticallyImplyLeading: true),
      body: Stack(
        children: [
          Positioned(
            top: 0.h,
            bottom: 240.h,
            left: 0,
            right: 0,
            child: Stack(
              children: [
                Positioned.fill(
                  child: Image.asset(
                    'assets/images/sample_cover.jpg',
                    fit: BoxFit.cover,
                  ),
                ),
                Positioned.fill(
                  top: 0,
                  left: 0,
                  right: 0,
                  bottom: 0,
                  child: Container(
                    height: 1.sh,
                    decoration: const BoxDecoration(
                      gradient: LinearGradient(
                        begin: Alignment.topCenter,
                        end: Alignment.bottomCenter,

                        stops: [0.0, 0.18, 0.5, 0.82, 1.0],
                        colors: [
                          AppColors.primary,
                          AppColors.transparent,
                          AppColors.transparent,
                          AppColors.transparent,
                          AppColors.primary,
                        ],
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),

          Container(
            padding: EdgeInsets.symmetric(horizontal: 15.w),
            width: 1.sw,
            height: 1.sh,
            child: Column(
              crossAxisAlignment: .start,
              mainAxisAlignment: .end,
              children: [
                Text(
                  'Song Title',
                  style: text.titleLarge!.copyWith(color: AppColors.white),
                ),
                SizedBox(height: 5.h),
                Text(
                  'Artist Name',
                  style: text.bodyMedium!.copyWith(color: AppColors.white),
                ),
                SizedBox(height: 15.h),
                ProgressBar(
                  progress: Duration(seconds: 5),
                  buffered: Duration(seconds: 10),
                  total: Duration(seconds: 240),
                  baseBarColor: AppColors.grey700,
                  bufferedBarColor: AppColors.grey400,
                  thumbRadius: 0,
                  thumbGlowRadius: 0,
                  progressBarColor: AppColors.white,
                  timeLabelPadding: 10.h,
                  timeLabelTextStyle: text.bodyMedium!.copyWith(
                    color: AppColors.grey400,
                  ),
                ),
                SizedBox(height: 15.h),
                Padding(
                  padding: EdgeInsets.symmetric(horizontal: 20.w),
                  child: Row(
                    mainAxisAlignment: .spaceBetween,
                    children: [
                      IconButton(
                        icon: Icon(
                          Icons.skip_previous,
                          color: AppColors.grey200,
                          size: 30.sp,
                        ),
                        onPressed: () {},
                      ),
                      IconButton(
                        style: IconButton.styleFrom(
                          backgroundColor: AppColors.white,
                        ),
                        icon: Icon(
                          isPlaying ? Icons.pause : Icons.play_arrow,
                          color: AppColors.primary,
                          size: 50.sp,
                        ),
                        onPressed: () {
                          setState(() {
                            isPlaying = !isPlaying;
                          });
                        },
                      ),
                      IconButton(
                        icon: Icon(
                          Icons.skip_next,
                          color: AppColors.grey200,
                          size: 30.sp,
                        ),
                        onPressed: () {},
                      ),
                    ],
                  ),
                ),
                SizedBox(height: kBottomNavigationBarHeight),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
