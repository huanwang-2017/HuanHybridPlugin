package com.huan.hhp.common;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.huan.hhp.HwServiceManager;
import com.huan.hhp.app.App_configManager;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by Administrator on 2016/10/20.
 */
public class BaseApplication extends Application {
    final String TAG = BaseApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(this);
        App_configManager.init(this);
        try {
            org.mozilla.javascript.Context.exit();
        }catch (Exception e){}
    }

    // 初始化imageLoader
    public void initImageLoader(Context context) {
        Log.i(TAG, "UIParserApplication initImageLoader...");
        File cacheDir = StorageUtils.getOwnCacheDirectory(context,
                "bee_k77/Cache");// 获取到缓存的目录地址
        Log.e("cacheDir", cacheDir.getPath());
        // 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                // max width, max height，即保存的每个缓存文件的最大长宽
                .memoryCacheExtraOptions(480, 800)
                // Can slow ImageLoader, use it carefully (Better don't use it)设置缓存的详细信息，最好不要设置这个
                //                .discCacheExtraOptions(480, 800, Bitmap.CompressFormat.JPEG, 75, null)
                // 线程池内加载的数量
                .threadPoolSize(3)
                // 线程优先级
                .threadPriority(Thread.NORM_PRIORITY - 2)
            /*
             * When you display an image in a small ImageView
             *  and later you try to display this image (from identical URI) in a larger ImageView
             *  so decoded image of bigger size will be cached in memory as a previous decoded image of smaller size.
             *  So the default behavior is to allow to cache multiple sizes of one image in memory.
             *  You can deny it by calling this method:
             *  so when some image will be cached in memory then previous cached size of this image (if it exists)
             *   will be removed from memory cache before.
             */
                //               .denyCacheImageMultipleSizesInMemory()

                // You can pass your own memory cache implementation你可以通过自己的内存缓存实现
                // .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                // .memoryCacheSize(2 * 1024 * 1024)
                //硬盘缓存50MB
                .diskCacheSize(50 * 1024 * 1024)
                //将保存的时候的URI名称用MD5
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                // 加密
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())//将保存的时候的URI名称用HASHCODE加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheFileCount(100) //缓存的File数量
                .diskCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
                // .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                // .imageDownloader(new BaseImageDownloader(context, 5 * 1000,
                // 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs()
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);// 全局初始化此配置
    }
}
