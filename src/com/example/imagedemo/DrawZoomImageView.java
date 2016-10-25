package com.example.imagedemo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义涂鸦控件，支持多点触控放大缩小、移动
 * 支持涂鸦、橡皮擦、自定义涂鸦颜色
 * @author openXu
 */
public class DrawZoomImageView extends View {
	private String TAG = "DrawZoomImageView";
	private int currentStatus; //记录当前操作的状态
	public static final int STATUS_INIT = 0; //初始化状态常量
	public static final int STATUS_NONE = 1;
	public static final int STATUS_ZOOM_OUT = 3; //图片放大状态常量
	public static final int STATUS_ZOOM_IN = 4; //图片缩小状态常量
	public static final int STATUS_TY = 10; //涂鸦
	public static final int STATUS_XP = 11; //橡皮
	public static final int STATUS_WORD = 12; //文字
	private ModeEnum mode = ModeEnum.TY; //模式（当先编辑是橡皮还是涂鸦）
	private Matrix matrix; //用于对图片进行移动和缩放变换的矩阵
	private Bitmap sourceBitmap; //传入的源图片
	private Bitmap bgBitmap; //用于涂鸦和擦除的背景图
	
	private int width; //DrawZoomImageView控件的宽度
	private int height; //DrawZoomImageView控件的高度
	private float centerPointX; //记录两指同时放在屏幕上时，中心点的横坐标值
	private float centerPointY; //记录两指同时放在屏幕上时，中心点的纵坐标值
	private int currentBitmapWidth; //记录当前图片的宽度，图片被缩放时，这个值会一起变动
	private int currentBitmapHeight; //记录当前图片的高度，图片被缩放时，这个值会一起变动
	private float movedDistanceX; //记录手指在横坐标方向上的移动距离
	private float movedDistanceY; //记录手指在纵坐标方向上的移动距离 
	private float totalTranslateX; //记录图片在矩阵上的横向偏移值
	private float totalTranslateY; //记录图片在矩阵上的纵向偏移值
	private float totalRatio; //记录图片在矩阵上的总缩放比例
	private float scaledRatio; //记录手指移动的距离所造成的缩放比例
	private float initRatio; //记录图片初始化时的缩放比例
	private double lastFingerDis; //记录上次两指之间的距离
	
	private Canvas mCanvas; //画布（用于记录涂鸦和擦除）
	private Paint mPaint; //画笔
	public int lineStrokeWidthMax = 30; //涂鸦画笔最大宽度
	public int xpStrokeWidthMax = 70; //橡皮擦画笔最大宽度
	private float lineStrokeWidth = 15f; //默认涂鸦画笔宽度
	private float xpStrokeWidth = 30f; //默认橡皮擦宽度
	private int mColor; //默认涂鸦画笔颜色
	private Path mPath; //涂鸦和擦除路径
	private DrawPath mDp; //记录Path路径的对象
	private List<DrawPath> mPathList; //保存当前Path路径的集合,用List集合来模拟栈
	private List<DrawPath> mAllPathList; //保存所有Path路径的集合,用List集合来模拟栈

	//涂鸦或擦除路径及参数记载
	public class DrawPath {
		public Path path;
		public Paint paint;
		public float ratio; //缩放比例
		public float strokeWidth; //画笔的宽度
	}
	public DrawZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init(){
		currentStatus = STATUS_INIT;
		matrix = new Matrix();
		mColor = 0xFFFF0000; //默认为红色
        mPathList = new ArrayList<DrawPath>();
		mAllPathList = new ArrayList<DrawPath>();
	}
	private void initLinePaint(){
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(lineStrokeWidth);
        mDp.strokeWidth = lineStrokeWidth;
	}
	private void initXpPaint() {
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);//这里只要不设置成Color.TRANSPARENT透明色就行，颜色任意；再设置画笔的模式
		mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
		mPaint.setAntiAlias(false);  
		mPaint.setStyle(Paint.Style.STROKE);  
		mPaint.setStrokeWidth(xpStrokeWidth);  
		mDp.strokeWidth = xpStrokeWidth;
    }
	
	/********************************↓↓↓↓接口调用↓↓↓↓*********************************/
	/**
	 * 编辑模式 
	 */
	public enum ModeEnum {
	    XP, TY;
	}
	
	/**
	 * 将待展示的图片设置进来。
	 * @param bitmap 待展示的Bitmap对象
	 */
	public void setImageBitmap(Bitmap bitmap) {
		currentStatus = STATUS_INIT;
		sourceBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true); 
		mPathList.clear();
		mAllPathList.clear();
		invalidate();
	}
	
	/**
	 * 设置涂鸦画笔宽度
	 * @param paintStrokeWidth
	 */
	public void setTyStrokeWidth(int paintStrokeWidth){
		lineStrokeWidth = paintStrokeWidth*1f;
	}
	public int getTyStrokeWidth(){
		return (int) lineStrokeWidth;
	}
	
	/**
	 * 设置橡皮宽度
	 * @param paintStrokeWidth
	 */
	public void setXpStrokeWidth(int paintStrokeWidth){
		xpStrokeWidth = paintStrokeWidth*1f;
	}
	public int getXpStrokeWidth(){
		return (int) xpStrokeWidth;
	}
	
	/**
	 * 设置涂鸦颜色
	 * @param Color
	 */
	public void setTyColor(int Color){
		mColor = Color;
	}
	
	/**
	 * 设置模式
	 */
	public void setMode(ModeEnum mode){
		this.mode = mode;
	}
	public ModeEnum getMode(){
		return mode;
	}
	
	/**
	 * 撤销一步（先将设置进来的图片覆盖历史背景，然后遍历线条集合，一条一条画进去）
	 */
	public void revoke(){
		if (currentStatus == STATUS_INIT) //还未植入图片
			return;
		MyUtil.LOG_V(TAG, "撤销前有" + mPathList.size() + "条线");
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //清空画布
        if(mPathList != null && mPathList.size() > 0){
        	mPathList.remove(mPathList.size() -1);
        	Iterator<DrawPath> iter = mPathList.iterator();
        	while (iter.hasNext()){
        		DrawPath drawPath = iter.next();
				drawPath.paint.setStrokeWidth(drawPath.strokeWidth / drawPath.ratio);//根据缩放比例，改变画笔粗细
        		mCanvas.drawPath(drawPath.path, drawPath.paint);
        	}
			invalidate(); //最后调用onDraw将画好的图片画入控件的canvas
        }
	}
	
	/**
	 * 恢复一步
	 */
	public void recovery(){
		if (currentStatus == STATUS_INIT) //还未植入图片
			return;
		MyUtil.LOG_V(TAG, "恢复前有" + mPathList.size() + "条线");
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //清空画布
		//画线
        if(mAllPathList != null && mAllPathList.size() > 0){
        	if(mPathList.size()<mAllPathList.size()){
        		mPathList.add(mAllPathList.get(mPathList.size()));
        		Iterator<DrawPath> iter = mPathList.iterator();
            	while (iter.hasNext()){
            		DrawPath drawPath = iter.next();
					drawPath.paint.setStrokeWidth(drawPath.strokeWidth / drawPath.ratio);//根据缩放比例，改变画笔粗细
            		mCanvas.drawPath(drawPath.path, drawPath.paint);
            	}
				invalidate(); //最后调用onDraw将画好的图片画入控件的canvas
        	}
        }
	}
	
	/**
	 * 获取涂鸦之后的位图
	 */
	public Bitmap getImageBitmap(){
		if (currentStatus == STATUS_INIT) //还未植入图片
			return null;
		//①、创建新的空画布
		Bitmap mBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(),sourceBitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmap);
		//②、将涂鸦和擦除的图片重新画到背景上
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //清空画布
		//将涂鸦和擦除的痕迹一条条画上去
        if(mPathList != null && mPathList.size() > 0){
        	Iterator<DrawPath> iter = mPathList.iterator();
        	while (iter.hasNext()){
				MyUtil.LOG_V(TAG, "划线");
        		DrawPath drawPath = iter.next();
				drawPath.paint.setStrokeWidth(drawPath.strokeWidth / drawPath.ratio);//根据缩放比例，改变画笔粗细
        		mCanvas.drawPath(drawPath.path, drawPath.paint);
        	}
        }
		//③、将源图画入新的画布中
        canvas.drawBitmap(sourceBitmap, new Matrix(), null);           
		//④、将背景图（透明背景涂鸦层）画到画布上
		canvas.drawBitmap(bgBitmap, new Matrix(), null);  
		//⑤、得到新的图片（空图-->画入源图-->画入涂鸦层）
		return mBitmap;  
	}
	
	/********************************↑↑↑↑↑接口调用↑↑↑↑↑*********************************/
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
			// 分别获取到ZoomImageView的宽度和高度
			width = getWidth();
			height = getHeight();
		}
	}

	boolean hasLine = false; //有没有划线
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (currentStatus == STATUS_INIT) //还没有设置图片
			return true;
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			if(event.getPointerCount() ==1){
				hasLine = true;
				float x = event.getX();
		        float y = event.getY();
				MyUtil.LOG_V(TAG, "单指按下x" + x + "  y" + y);
		        x = (x-totalTranslateX)/totalRatio;
		        y = (y-totalTranslateY)/totalRatio;
				currentStatus = (mode==ModeEnum.XP?STATUS_XP : STATUS_TY);
				mPath = new Path();
				mDp = new DrawPath();
				mDp.path = mPath;
				MyUtil.LOG_V(TAG, "手指放下，当前线术" + mPathList.size());
				if(currentStatus == STATUS_TY){
					initLinePaint();
				}else if(currentStatus == STATUS_XP){
					initXpPaint();
				}
				mDp.paint = mPaint;
				//单指触摸，为编辑模式
                mPath.moveTo(x, y);
			}
            break;
		case MotionEvent.ACTION_POINTER_DOWN: //多于一个手指按下
			if (event.getPointerCount() == 2) {
				hasLine = false;
				//初始化移动中心点
				float xPoint0 = event.getX(0);
				float yPoint0 = event.getY(0);
				float xPoint1 = event.getX(1);
				float yPoint1 = event.getY(1);
				lastCenterPointX = (xPoint0 + xPoint1) / 2;
				lastCenterPointY = (yPoint0 + yPoint1) / 2;
				// 当有两个手指按在屏幕上时，计算两指之间的距离
				lastFingerDis = distanceBetweenFingers(event);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (event.getPointerCount() == 1) {
				hasLine = true;
				MyUtil.LOG_V(TAG, "单指滑动，开始画画");
				 float move_x = event.getX();
	             float move_y = event.getY();
				//查看Matrix原理（矩阵操作）
				move_x = (move_x - totalTranslateX) / totalRatio; //还原平移
	             move_y = (move_y-totalTranslateY)/totalRatio;
				mPath.lineTo(move_x, move_y);//移动
                if (currentStatus == STATUS_TY) {
					mPaint.setStrokeWidth(lineStrokeWidth / totalRatio); //将画笔变细
                }else if (currentStatus == STATUS_XP) {
					mPaint.setStrokeWidth(xpStrokeWidth / totalRatio); //将画笔变细
                }
				mCanvas.drawPath(mPath, mPaint); //划线
			} else if (event.getPointerCount() == 2) {
				hasLine = false;
				// 有两个手指按在屏幕上移动时，为缩放状态
				centerPointBetweenFingers(event);
				double fingerDis = distanceBetweenFingers(event);
				if (fingerDis > lastFingerDis) {
					currentStatus = STATUS_ZOOM_OUT;
				} else {
					currentStatus = STATUS_ZOOM_IN;
				}
				// 进行缩放倍数检查，最大只允许将图片放大4倍，最小可以缩小到初始化比例
				if ((currentStatus == STATUS_ZOOM_OUT && totalRatio < 4 * initRatio)
						|| (currentStatus == STATUS_ZOOM_IN && totalRatio > initRatio)) {
					scaledRatio = (float) (fingerDis / lastFingerDis);
					totalRatio = totalRatio * scaledRatio;
					if (totalRatio > 4 * initRatio) {
						totalRatio = 4 * initRatio;
					} else if (totalRatio < initRatio) {
						totalRatio = initRatio;
					}
					lastFingerDis = fingerDis;
				}
				// 进行边界检查，不允许将图片拖出边界
				if (totalTranslateX + movedDistanceX > 0) {
					movedDistanceX = 0;
				} else if (width - (totalTranslateX + movedDistanceX) > currentBitmapWidth) {
					movedDistanceX = 0;
				}
				if (totalTranslateY + movedDistanceY > 0) {
					movedDistanceY = 0;
				} else if (height - (totalTranslateY + movedDistanceY) > currentBitmapHeight) {
					movedDistanceY = 0;
				}
				lastCenterPointX = centerPointX;
				lastCenterPointY = centerPointY;
			}
			break;
		case MotionEvent.ACTION_POINTER_UP: //当第二根手指抬起
			if(event.getPointerCount() ==1){
				//编辑
				float x = event.getX();
		        float y = event.getY();
				if(hasLine){
					MyUtil.LOG_D(TAG, "保存有效线条");
					MyUtil.LOG_V(TAG, "手指抬起了，将线条放入集合，数量" + mPathList.size());
					mDp.ratio = totalRatio; //保存缩放比例
	            	mPathList.add(mDp);
	    			mAllPathList.add(mDp);
	            }
				
				x = (x-totalTranslateX)/totalRatio;
		        y = (y-totalTranslateY)/totalRatio;
				currentStatus = (mode==ModeEnum.XP?STATUS_XP : STATUS_TY);
				mPath = new Path();
				mDp = new DrawPath();
				mDp.path = mPath;
				MyUtil.LOG_V(TAG, "手指放下，当前线术" + mPathList.size());
				if(currentStatus == STATUS_TY){
					initLinePaint();
				}else if(currentStatus == STATUS_XP){
					initXpPaint();
				}
				mDp.paint = mPaint;
				//单指触摸，为编辑模式
                mPath.moveTo(x, y);
				
			}
			// 手指离开屏幕时将临时值还原
			lastCenterPointX = -1;
			lastCenterPointY = -1;
			break;
		case MotionEvent.ACTION_UP:
			// 手指离开屏幕时将临时值还原
			lastCenterPointX = -1;
			lastCenterPointY = -1;
			
			if(hasLine){
				MyUtil.LOG_D(TAG, "保存有效线条");
				MyUtil.LOG_V(TAG, "手指抬起了，将线条放入集合，数量" + mPathList.size());
				mDp.ratio = totalRatio; //保存缩放比例
            	mPathList.add(mDp);
    			mAllPathList.add(mDp);
            }
			mPath = null;
			break;
		default:
			break;
		}
		// 调用onDraw()方法绘制图片
		invalidate();
		return true;
	}

	/**
	 * 根据currentStatus的值来决定对图片进行什么样的绘制操作。
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		switch (currentStatus) {
		case STATUS_TY:
			break;
		case STATUS_XP:
			break;
		case STATUS_ZOOM_OUT:
		case STATUS_ZOOM_IN:
			zoomAndMove(canvas);
			break;
		case STATUS_INIT:
			MyUtil.LOG_V(TAG, "初始化图片");
			initBitmap(canvas);
			break;
		}
		if(bgBitmap!=null&& sourceBitmap!=null){
			canvas.drawBitmap(sourceBitmap, matrix, null);//将源图画入画布
			canvas.drawBitmap(bgBitmap, matrix, null); //将涂鸦的背景画入画布
		}
		
	}
	
	/**
	 * 对图片进行缩放和平移处理。
	 * @param canvas
	 */
	private void zoomAndMove(Canvas canvas){
		matrix.reset();
		// 将图片按总缩放比例进行缩放
		matrix.postScale(totalRatio, totalRatio);
		float scaledWidth = sourceBitmap.getWidth() * totalRatio;
		float scaledHeight = sourceBitmap.getHeight() * totalRatio;
		float translateX = 0f;
		float translateY = 0f;
		
		// 如果当前图片宽度小于屏幕宽度，则按屏幕中心的横坐标进行水平缩放。否则按两指的中心点的横坐标进行水平缩放
		if (currentBitmapWidth < width) {
			translateX = (width - scaledWidth) / 2f;
		} else {
			translateX = totalTranslateX * scaledRatio + centerPointX * (1 - scaledRatio);
			translateX = translateX + movedDistanceX; //加上中心点移动距离
			// 进行边界检查，保证图片缩放后在水平方向上不会偏移出屏幕
			if (translateX > 0) {
				translateX = 0;
			} else if (width - translateX > scaledWidth) {
				translateX = width - scaledWidth;
			}
		}
		// 如果当前图片高度小于屏幕高度，则按屏幕中心的纵坐标进行垂直缩放。否则按两指的中心点的纵坐标进行垂直缩放
		if (currentBitmapHeight < height) {
			translateY = (height - scaledHeight) / 2f;
		} else {
			translateY = totalTranslateY * scaledRatio + centerPointY* (1 - scaledRatio);
			translateY = translateY + movedDistanceY; //加上中心点移动距离
			// 进行边界检查，保证图片缩放后在垂直方向上不会偏移出屏幕
			if (translateY > 0) {
				translateY = 0;
			} else if (height - translateY > scaledHeight) {
				translateY = height - scaledHeight;
			}
		}
		// 缩放后对图片进行偏移，以保证缩放后中心点位置不变
		matrix.postTranslate(translateX, translateY);
		totalTranslateX = translateX;
		totalTranslateY = translateY;
		currentBitmapWidth = (int) scaledWidth;
		currentBitmapHeight = (int) scaledHeight;
		
	}
	
	/**
	 * 对图片进行初始化操作，包括让图片居中，以及当图片大于屏幕宽高时对图片进行压缩。
	 * @param canvas
	 */
	private void initBitmap(Canvas canvas) {
		if (sourceBitmap != null) {
			matrix.reset();
			int bitmapWidth = sourceBitmap.getWidth();
			int bitmapHeight = sourceBitmap.getHeight();
			MyUtil.LOG_V(TAG, "图片的宽高" + bitmapWidth + ", " + bitmapHeight + "，控件" + width + "," + height);
			if (bitmapWidth > width || bitmapHeight > height) {
				if (bitmapWidth - width > bitmapHeight - height) {
					MyUtil.LOG_V(TAG, bitmapWidth + "图片的宽度太大了" + width);
					// 当图片宽度大于屏幕宽度时，将图片等比例压缩，使它可以完全显示出来
					float ratio = width / (bitmapWidth * 1.0f);
					MyUtil.LOG_V(TAG, bitmapWidth + "图片的宽度太大了" + width + "，按照宽度缩放" + ratio);
					matrix.postScale(ratio, ratio);
					float translateY = (height - (bitmapHeight * ratio)) / 2f;
					// 在纵坐标方向上进行偏移，以保证图片居中显示
					matrix.postTranslate(0, translateY);
					totalTranslateY = translateY;
					totalRatio = initRatio = ratio;
				} else {
					// 当图片高度大于屏幕高度时，将图片等比例压缩，使它可以完全显示出来
					float ratio = height / (bitmapHeight * 1.0f);
					MyUtil.LOG_V(TAG, bitmapWidth + "图片的高度太大了" + width + "，按照高度缩放" + ratio);
					matrix.postScale(ratio, ratio);
					float translateX = (width - (bitmapWidth * ratio)) / 2f;
					// 在横坐标方向上进行偏移，以保证图片居中显示
					matrix.postTranslate(translateX, 0);
					totalTranslateX = translateX;
					totalRatio = initRatio = ratio;
				}
				currentBitmapWidth = (int) (bitmapWidth * initRatio);
				currentBitmapHeight = (int) (bitmapHeight * initRatio);
			} else {
				// 当图片的宽高都小于屏幕宽高时，直接让图片居中显示
				float translateX = (width - bitmapWidth) / 2f;
				float translateY = (height - bitmapHeight) / 2f;
				MyUtil.LOG_V(TAG, "图片的宽高都小于屏幕宽高" + "，X平移" + translateX + ",Y平移" + translateY);
				matrix.postTranslate(translateX, translateY);
				totalTranslateX = translateX;
				totalTranslateY = translateY;
				totalRatio = initRatio = 1f;
				currentBitmapWidth = bitmapWidth;
				currentBitmapHeight = bitmapHeight;
			}
			// 防止出现Immutable bitmap passed to Canvas constructor错误  
			MyUtil.LOG_V(TAG, "压缩后图片宽高" + currentBitmapWidth + " " + currentBitmapHeight);
			//创建一副原图大小相同的透明背景图片
			bgBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(),sourceBitmap.getHeight(), Config.ARGB_8888);
//			bgBitmap = Bitmap.createBitmap(sourceBitmap,0,0,sourceBitmap.getWidth(),sourceBitmap.getHeight());
			mCanvas = new Canvas(bgBitmap);
			MyUtil.LOG_V(TAG, "创建一个宽高为" + mCanvas.getWidth() + " " + mCanvas.getHeight() + "的画布");
			
			currentStatus = STATUS_NONE;
		}
	}

	/**
	 * 计算两个手指之间的距离。
	 * @param event
	 * @return 两个手指之间的距离
	 */
	private double distanceBetweenFingers(MotionEvent event) {
		float disX = Math.abs(event.getX(0) - event.getX(1));
		float disY = Math.abs(event.getY(0) - event.getY(1));
		return Math.sqrt(disX * disX + disY * disY);
	}

	//记录上一次移动后两手指间中心点的坐标
	private float lastCenterPointX = -1;
	private float lastCenterPointY = -1;
	
	/**
	 * 计算两个手指之间中心点的坐标。
	 * @param event
	 */
	private void centerPointBetweenFingers(MotionEvent event) {
		float xPoint0 = event.getX(0);
		float yPoint0 = event.getY(0);
		float xPoint1 = event.getX(1);
		float yPoint1 = event.getY(1);
		centerPointX = (xPoint0 + xPoint1) / 2;
		centerPointY = (yPoint0 + yPoint1) / 2;
		
		if (lastCenterPointX != -1 && lastCenterPointY != -1) {
			movedDistanceX = centerPointX - lastCenterPointX;
			movedDistanceY = centerPointY - lastCenterPointY;
		}
	}
	

}
