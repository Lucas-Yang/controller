package com.example.myapplication;

import android.graphics.Bitmap;

public interface DataListener {
	void onPicIn(Bitmap bufferedImage);
	void sendCommand(int t);
}
