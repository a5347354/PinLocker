package net.funol.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.Random;

/**
 * Created by fan on 15/7/31.
 * @author Chun-Hao Fan
 */
public class LockAlgorithm {

    int blkSize;//BlockSize拆解區塊大小
    int RndCnt;
    Bitmap srcBmp;//需要鎖圖或解鎖
    int seed;

    /**
     * 建構子
     * @param randSeed   亂數種子
     * */
    public LockAlgorithm(int randSeed){
        blkSize = 16;
        seed = randSeed;
    }
    /**
     * 建構子
     * @param lockBitmap 需要Lock或unLock的圖片Bitmap
     * @param randSeed   亂數種子
     * */
    public LockAlgorithm(Bitmap lockBitmap,int randSeed){
        blkSize = 16;
        srcBmp = lockBitmap;
        seed = randSeed;
    }


    /**
     * 建構子
     * @param lockBitmap 需要Lock或unLock的圖片Bitmap
     * @param randSeed   亂數種子
     * @param blockSize  每個區塊大小，需為2的倍數，否則為16
     * */
    public LockAlgorithm(Bitmap lockBitmap,int randSeed,int blockSize){
        srcBmp = lockBitmap;
        seed = randSeed;
        if (blockSize % 2 == 0)
            blkSize = blockSize;
        else
            blkSize = 16;
    }


    /**
     * 鎖圖
     * @return 鎖圖完的圖片
     * */
    public  Bitmap LockBitmap(){

        int PicWidth = srcBmp.getWidth() , PicHeight = srcBmp.getHeight();
        int BlksX , BlksY ,TotalBlks , BlkPixels;

        BlksX = (int) PicWidth / blkSize;
        BlksY = (int) PicHeight / blkSize;
        TotalBlks = BlksX * BlksY;
        BlkPixels = blkSize * blkSize;

        RndCnt = 0;
        Random Rnd = new Random(seed);

        int RndBlkX , RndBlkY , RndBlk;

        int LessWidth = PicWidth % blkSize;

        Log.d("LockBitmap", "LessWidth = " + String.valueOf(LessWidth));

        if(LessWidth > 0){                      //右方寬度不足 blkSize 的部分
            for(int CurrentBlkY = 0 ; CurrentBlkY < BlksY ; CurrentBlkY++){
                RndBlkY = (int)Rnd.nextInt(BlksY);
                RndCnt++;

                // Block 互調
                int[] CurrentBlk = new int[LessWidth * blkSize];
                srcBmp.getPixels(CurrentBlk, 0, LessWidth, BlksX * blkSize , CurrentBlkY * blkSize, LessWidth, blkSize);

                int[] ChangeBlk = new int[LessWidth * blkSize];
                srcBmp.getPixels(ChangeBlk, 0, LessWidth, BlksX * blkSize , RndBlkY * blkSize, LessWidth, blkSize);

                srcBmp.setPixels(ChangeBlk, 0, LessWidth, BlksX * blkSize , CurrentBlkY * blkSize, LessWidth, blkSize);
                srcBmp.setPixels(CurrentBlk, 0, LessWidth, BlksX * blkSize , RndBlkY * blkSize, LessWidth, blkSize);
            }
        }

        int LessHeight = PicHeight % blkSize;

        Log.d("LockBitmap" , "LessHeight = " + String.valueOf(LessHeight));

        if(LessHeight > 0){                      //底部高度不足 blkSize 的部分
            for(int CurrentBlkX = 0 ; CurrentBlkX < BlksX ; CurrentBlkX++){
                RndBlkX = (int)Rnd.nextInt(BlksX);
                RndCnt++;

                // Block 互調
                int[] CurrentBlk = new int[blkSize * LessHeight];
                srcBmp.getPixels(CurrentBlk, 0, blkSize, CurrentBlkX * blkSize , BlksY * blkSize, blkSize, LessHeight);

                int[] ChangeBlk = new int[blkSize * LessHeight];
                srcBmp.getPixels(ChangeBlk, 0, blkSize, RndBlkX * blkSize , BlksY * blkSize, blkSize, LessHeight);

                srcBmp.setPixels(ChangeBlk, 0, blkSize, CurrentBlkX * blkSize , BlksY * blkSize, blkSize, LessHeight);
                srcBmp.setPixels(CurrentBlk, 0, blkSize, RndBlkX * blkSize , BlksY * blkSize, blkSize, LessHeight);
            }
        }

        for(int CurrentBlkX = 0 ; CurrentBlkX < BlksX ; CurrentBlkX++)
            for(int CurrentBlkY = 0 ; CurrentBlkY < BlksY ; CurrentBlkY++){
                RndBlk = (int)Rnd.nextInt(TotalBlks);
                RndCnt++;
                RndBlkX = RndBlk % BlksX;  // 需再確認 OK 不 OK
                RndBlkY = RndBlk / BlksX;

                // Block 互調
                int[] CurrentBlk = new int[BlkPixels];
                srcBmp.getPixels(CurrentBlk, 0, blkSize, CurrentBlkX * blkSize , CurrentBlkY * blkSize, blkSize, blkSize);

                int[] ChangeBlk = new int[BlkPixels];
                srcBmp.getPixels(ChangeBlk, 0, blkSize, RndBlkX * blkSize , RndBlkY * blkSize, blkSize, blkSize);

                srcBmp.setPixels(ChangeBlk, 0, blkSize, CurrentBlkX * blkSize , CurrentBlkY * blkSize, blkSize, blkSize);
                srcBmp.setPixels(CurrentBlk, 0, blkSize, RndBlkX * blkSize , RndBlkY * blkSize, blkSize, blkSize);
            }

        return srcBmp;
    }








    /**
     * 解圖
     * @return 解圖完的原圖
     * */
    public Bitmap UnLockBitmap(){

        int PicWidth = srcBmp.getWidth() , PicHeight = srcBmp.getHeight();
        int BlksX , BlksY ,TotalBlks , BlkPixels;

        BlksX = (int) PicWidth / blkSize;
        BlksY = (int) PicHeight / blkSize;
        TotalBlks = BlksX * BlksY;
        BlkPixels = blkSize * blkSize;

        //RndCnt = 0;
        Random Rnd = new Random(seed);
        int ArrayLength = TotalBlks ;

        int LessHeight = PicHeight % blkSize;
        if(LessHeight > 0){
            ArrayLength += BlksX;
        }

        int LessWidth = PicWidth % blkSize;
        if(LessWidth > 0){
            ArrayLength += BlksY;
        }

        int[] RndOrder = new int[ArrayLength];
        int CurrentIndex = 0;
        // 以相同的亂樹種子，先產生鎖圖時相同的亂數數列
        if(LessHeight > 0){
            for(int Cnt = CurrentIndex ; Cnt < BlksY ; Cnt++){
                RndOrder[Cnt] = (int) Rnd.nextInt(BlksY);
                RndCnt--;
            }
            CurrentIndex = BlksY;
        }

        if(LessWidth > 0){
            for(int Cnt = CurrentIndex ; Cnt < BlksX + CurrentIndex ; Cnt++){
                RndOrder[Cnt] = (int) Rnd.nextInt(BlksX);
                RndCnt--;
            }
            CurrentIndex += BlksX;
        }

        for(int Cnt = CurrentIndex ; Cnt < TotalBlks + CurrentIndex ; Cnt++){
            RndOrder[Cnt] = (int) Rnd.nextInt(TotalBlks);
            RndCnt--;
        }

        Log.d("UnLockBitmap" , "RndCnt = " + String.valueOf(RndCnt));

        int RndBlkX , RndBlkY , RndBlk;
        int RndOrderIndex = RndOrder.length - 1;

        Log.d("UnLockBitmap" , "Before For Loop");

        // 依照逆向的方式 ，將鎖圖的各個畫素對調回原來的位置
        //for(int CurrentBlk = RndOrder.length - 1 ; CurrentBlk > -1 ; CurrentBlk--){
        for(int CurrentBlkX = BlksX -1 ; CurrentBlkX > -1 ; CurrentBlkX--)
            for(int CurrentBlkY = BlksY - 1 ; CurrentBlkY > -1 ; CurrentBlkY--){
                RndBlk = RndOrder[RndOrderIndex];
                RndOrderIndex--;
                RndBlkX = RndBlk % BlksX;  // 需再確認 OK 不 OK
                RndBlkY = RndBlk / BlksX;

                // Block 互調
                int[] CurrentBlk = new int[BlkPixels];
                srcBmp.getPixels(CurrentBlk, 0, blkSize, CurrentBlkX * blkSize , CurrentBlkY * blkSize, blkSize, blkSize);

                int[] ChangeBlk = new int[BlkPixels];
                srcBmp.getPixels(ChangeBlk, 0, blkSize, RndBlkX * blkSize , RndBlkY * blkSize, blkSize, blkSize);

                srcBmp.setPixels(ChangeBlk, 0, blkSize, CurrentBlkX * blkSize , CurrentBlkY * blkSize, blkSize, blkSize);
                srcBmp.setPixels(CurrentBlk, 0, blkSize, RndBlkX * blkSize , RndBlkY * blkSize, blkSize, blkSize);
            }

        Log.d("UnLockBitmap" , "Before LessHeight > 0");

        //int LessHeight = PicHeight % blkSize;
        if(LessHeight > 0){                   //底部高度不足 blkSize 的部分
            for(int CurrentBlkX = BlksX - 1 ; CurrentBlkX > -1 ; CurrentBlkX--){
                RndBlkX = RndOrder[RndOrderIndex];
                RndOrderIndex--;

                // Block 互調
                int[] CurrentBlk = new int[blkSize * LessHeight];
                srcBmp.getPixels(CurrentBlk, 0, blkSize, CurrentBlkX * blkSize , BlksY * blkSize, blkSize, LessHeight);

                int[] ChangeBlk = new int[blkSize * LessHeight];
                srcBmp.getPixels(ChangeBlk, 0, blkSize, RndBlkX * blkSize , BlksY * blkSize, blkSize, LessHeight);

                srcBmp.setPixels(ChangeBlk, 0, blkSize, CurrentBlkX * blkSize , BlksY * blkSize, blkSize, LessHeight);
                srcBmp.setPixels(CurrentBlk, 0, blkSize, RndBlkX * blkSize , BlksY * blkSize, blkSize, LessHeight);
            }
        }

        Log.d("UnLockBitmap" , "Before LessWidth > 0");

        //int LessWidth = PicWidth % blkSize;
        if(LessWidth > 0){                    //右方寬度不足 blkSize 的部分
            for(int CurrentBlkY = BlksY - 1 ; CurrentBlkY > -1 ; CurrentBlkY--){
                RndBlkY = RndOrder[RndOrderIndex];
                RndOrderIndex--;

                // Block 互調
                int[] CurrentBlk = new int[LessWidth * blkSize];
                srcBmp.getPixels(CurrentBlk, 0, LessWidth, BlksX * blkSize , CurrentBlkY * blkSize, LessWidth, blkSize);

                int[] ChangeBlk = new int[LessWidth * blkSize];
                srcBmp.getPixels(ChangeBlk, 0, LessWidth, BlksX * blkSize , RndBlkY * blkSize, LessWidth, blkSize);

                srcBmp.setPixels(ChangeBlk, 0, LessWidth, BlksX * blkSize , CurrentBlkY * blkSize, LessWidth, blkSize);
                srcBmp.setPixels(CurrentBlk, 0, LessWidth, BlksX * blkSize , RndBlkY * blkSize, LessWidth, blkSize);
            }
        }

        return srcBmp;
    }

    @SuppressLint("NewApi")
    public void loadBitmap(String FilePath) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        if(srcBmp != null){
            srcBmp.recycle();
            System.gc();
        }
        opts.inMutable = true;  // return a mutable Bitmap instead of an immutable one.
        srcBmp = BitmapFactory.decodeFile(FilePath , opts);
    }

}
