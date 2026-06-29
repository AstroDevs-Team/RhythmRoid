import 'package:go_router/go_router.dart';
import 'package:rhythmroid/features/connection/presentation/connection_page.dart';
import 'package:rhythmroid/features/player/player_page.dart';

class AppRoutes {
  static const String connectionPage = '/connection';

  static const String playerPage = '/player';
}

class AppRouter {
  static final router = GoRouter(
    initialLocation: AppRoutes.connectionPage,
    routes: [
      GoRoute(
        path: AppRoutes.connectionPage,
        builder: (context, state) => const ConnectionPage(),
      ),
      GoRoute(
        path: AppRoutes.playerPage,
        builder: (context, state) => const PlayerPage(),
      ),
    ],
  );
}
