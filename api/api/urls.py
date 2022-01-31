from django.urls import path
from . import views

urlpatterns = [
    path('', views.initial, name='initial'),
    path('play_actions', views.play_actions, name='play_actions'),
    path('playing_info', views.playing_status, name='playing_info'),
]