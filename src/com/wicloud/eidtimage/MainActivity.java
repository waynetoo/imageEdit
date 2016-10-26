package com.wicloud.eidtimage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.wicloud.editimage.demo.R;

public class MainActivity extends Activity implements OnClickListener, OnCheckedChangeListener {

	private DrawZoomImageView iv_photo;
	private RelativeLayout rl_contrl;
	private ImageView iv_result;
	private LinearLayout ll_edit;
	private TextView bar_title, tv_open, tv_finish;
	private Button btn_revoke, btn_recovery;
	private RadioGroup colorGroup, action;
	private boolean isBack = true;
	private SeekBar seekBar; // 控制画笔宽度
	
	private RadioButton rbTy, rbXp, rbWord;
	private int currStatu;//当前状态；

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.img_edit_main);
		iv_photo = (DrawZoomImageView) findViewById(R.id.iv_photo);
		iv_result = (ImageView) findViewById(R.id.iv_result);
		rl_contrl = (RelativeLayout) findViewById(R.id.rl_contrl);
		rl_contrl.setVisibility(View.VISIBLE);
		iv_result.setVisibility(View.GONE);
		bar_title = (TextView) findViewById(R.id.bar_title);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
//		ll_color = (LinearLayout) findViewById(R.id.ll_color);
		ll_edit = (LinearLayout) findViewById(R.id.ll_edit);

		seekBar.setMax(iv_photo.lineStrokeWidthMax);
		seekBar.setProgress(iv_photo.getTyStrokeWidth());
		iv_photo.setTyStrokeWidth(seekBar.getProgress());
		currStatu = DrawZoomImageView.STATUS_TY;
		bar_title.setTextColor(Color.RED);
		iv_photo.setCurrentStatus(currStatu);

		tv_open = (TextView) findViewById(R.id.tv_open);
		btn_revoke = (Button) findViewById(R.id.btn_revoke);
		btn_recovery = (Button) findViewById(R.id.btn_recovery);
		tv_finish = (TextView) findViewById(R.id.tv_finish);
		
		rbTy = (RadioButton) findViewById(R.id.rb_ty);
		rbXp = (RadioButton) findViewById(R.id.rb_xp);
		rbWord = (RadioButton) findViewById(R.id.rb_word);

		tv_open.setOnClickListener(this);
		btn_revoke.setOnClickListener(this);
		btn_recovery.setOnClickListener(this);
		tv_finish.setOnClickListener(this);

		colorGroup = (RadioGroup) findViewById(R.id.color);
		colorGroup.setOnCheckedChangeListener(this);
		action = (RadioGroup) findViewById(R.id.action);
		action.setOnCheckedChangeListener(this);

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					if (currStatu == DrawZoomImageView.STATUS_TY) {
						iv_photo.setTyStrokeWidth(seekBar.getProgress());

					} else if (currStatu == DrawZoomImageView.STATUS_XP) {
						iv_photo.setXpStrokeWidth(seekBar.getProgress());

					} else if (currStatu == DrawZoomImageView.STATUS_WORD) {
						iv_photo.setWordStrokeWidth(seekBar.getProgress() + 8);
						bar_title.setText(String.valueOf(progress + 8));
					}
				}
			}
		});

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (group.equals(colorGroup)) {
			switch (checkedId) {
			case R.id.rb_xp:
//				ll_color.setVisibility(View.GONE);
				bar_title.setTextColor(Color.BLACK);
				break;
			case R.id.green:
				iv_photo.setTyColor(Color.GREEN);
				bar_title.setTextColor(Color.GREEN);
				break;
			case R.id.blue:
				iv_photo.setTyColor(Color.BLUE);
				bar_title.setTextColor(Color.BLUE);
				break;
			case R.id.yellow:
				iv_photo.setTyColor(Color.YELLOW);
				bar_title.setTextColor(Color.YELLOW);
				break;
			case R.id.red:
				iv_photo.setTyColor(Color.RED);
				bar_title.setTextColor(Color.RED);
				break;
			case R.id.black:
				iv_photo.setTyColor(Color.BLACK);
				bar_title.setTextColor(Color.BLACK);
				break;
			case R.id.white:
				iv_photo.setTyColor(Color.WHITE);
				bar_title.setTextColor(Color.WHITE);
				break;
			default:
				iv_photo.setTyColor(Color.BLACK);
				bar_title.setTextColor(Color.BLACK);
				break;
			}

		} else if (group.equals(action)) {
			switch (checkedId) {
			case R.id.rb_ty:
				ll_edit.setVisibility(View.VISIBLE);

				break;
			case R.id.rb_word:

				break;
			}
		}

		if (currStatu != getCurrStatus()) {
			currStatu = getCurrStatus();
			//设置状态
			iv_photo.setCurrentStatus(currStatu);
			changeSeekBar(currStatu);
		}
	}

	/**
	 *  当前编辑图片状态
	 * @return 
	 */
	public int getCurrStatus(){
		if (rbXp.isChecked()) {
			return DrawZoomImageView.STATUS_XP;
		} else if (rbTy.isChecked()) {
			return DrawZoomImageView.STATUS_TY;
		} else if (rbWord.isChecked()) {
			return DrawZoomImageView.STATUS_WORD;
		}
		return DrawZoomImageView.STATUS_TY;
	}

	public void changeSeekBar(int statu) {
		if (statu == DrawZoomImageView.STATUS_XP) {
			bar_title.setText("橡皮宽度");
			seekBar.setMax(iv_photo.xpStrokeWidthMax); // 设置最大
			seekBar.setProgress(iv_photo.getXpStrokeWidth());
		} else if (statu == DrawZoomImageView.STATUS_TY) {
			bar_title.setText("画笔宽度");
			seekBar.setMax(iv_photo.lineStrokeWidthMax); // 设置最大
			seekBar.setProgress(iv_photo.getTyStrokeWidth());
		} else if (statu == DrawZoomImageView.STATUS_WORD) {
			bar_title.setText(String.valueOf(iv_photo.getWordStrokeWidth()));
			seekBar.setMax(iv_photo.wordStrokeWidthMax); // 设置最大
			seekBar.setProgress(iv_photo.getWordStrokeWidth() - 8);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			Uri uri = data.getData();
			try {
				String[] proj = {MediaStore.Images.Media.DATA};
				//好像是android多媒体数据库的封装接口，具体的看Android文档
	            Cursor cursor = managedQuery(uri, proj, null, null, null); 
				//按我个人理解 这个是获得用户选择的图片的索引值
	            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				//将光标移至开头 ，这个很重要，不小心很容易引起越界
	            cursor.moveToFirst();
				//最后根据索引值获取图片路径
	            String path = cursor.getString(column_index);
				
				Bitmap bitmap = readBitmapAutoSize(path, iv_photo.getWidth(), iv_photo.getHeight());
				iv_photo.setImageBitmap(bitmap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_open: // 打开图片
			Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
			intent2.setType("image/*");
			startActivityForResult(intent2, 2);
			break;
		case R.id.btn_revoke: // 撤销
			iv_photo.revoke();
			break;
		case R.id.btn_recovery: // 恢复
			iv_photo.recovery();
			break;
		case R.id.tv_finish: // 查看编辑好的图片
			rl_contrl.setVisibility(View.GONE);
			iv_result.setVisibility(View.VISIBLE);
			Bitmap bitmap = iv_photo.getImageBitmap();
			iv_result.setImageBitmap(bitmap);
			isBack = false;
			break;

		default:
			break;
		}
	}

	public Bitmap readBitmapAutoSize(String filePath, int outWidth, int outHeight) {
		// outWidth和outHeight是目标图片的最大宽度和高度，用作限制
		FileInputStream fs = null;
		BufferedInputStream bs = null;
		try {
			fs = new FileInputStream(filePath);
			bs = new BufferedInputStream(fs);
			BitmapFactory.Options options = setBitmapOption(filePath, outWidth,
					outHeight);
			return BitmapFactory.decodeStream(bs, null, options);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bs.close();
				fs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private BitmapFactory.Options setBitmapOption(String file, int width, int height) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		// 设置只是解码图片的边距，此操作目的是度量图片的实际宽度和高度
		BitmapFactory.decodeFile(file, opt);

		int outWidth = opt.outWidth; // 获得图片的实际高和宽
		int outHeight = opt.outHeight;
		opt.inDither = false;
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		// 设置加载图片的颜色数为16bit，默认是RGB_8888，表示24bit颜色和透明通道，但一般用不上
		opt.inSampleSize = 1;
		// 设置缩放比,1表示原比例，2表示原来的四分之一....
		// 计算缩放比
		if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
			int sampleSize = (outWidth / width + outHeight / height) / 2;
			opt.inSampleSize = sampleSize;
		}
		opt.inJustDecodeBounds = false;// 最后把标志复原
		return opt;
	}

	@Override
	public void onBackPressed() {
		if (isBack) {
			super.onBackPressed();
		} else {
			iv_result.setVisibility(View.GONE);
			rl_contrl.setVisibility(View.VISIBLE);
			isBack = true;
		}

	}
}
