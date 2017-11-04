# Listen All   
## 简介
`Listen All` 是一款汇集 QQ 音乐和虾米音乐平台资源的 Android 音乐播放器。   
项目采用 `MVP` 架构，使用 [Kotlin](https://kotlinlang.org/docs/reference/) 开发。

它来自于一款 `Chrome` 浏览器插件 [Listen 1](https://github.com/listen1/listen1_chrome_extension)，所有的数据接口都是从这个项目中获取的，非常感谢。

本来打算像 `Listen 1` 一样添加 QQ 音乐、虾米音乐和网易云音乐三个平台的资源（这也是为什么叫 Listen All 的原因），但是最近 QQ 音乐与虾米音乐共享版权后，网易云音乐的资源就先待定了。

目前应用只添加了虾米音乐的资源，主要是想先把主要功能做起来，再添加 QQ 音乐的资源。
应用界面主要借鉴了主流音乐播放器，并添加了一些自己的设计；功能目前实现了大部分，还有小部分功能未做，比如歌曲缓存、下载、歌词显示和自建歌单等，所以目前的版本定为 `V 1.0.0 Preview`.    

应用还有很多不足，我还在继续努力开发和完善中....

## 开源框架
- [ButterKnife](https://github.com/JakeWharton/butterknife)
- [Glide](https://github.com/bumptech/glide)
- [greenDAO](https://github.com/greenrobot/greenDAO)
- [OkHttp](https://github.com/square/okhttp)
- [jsoup](https://github.com/jhy/jsoup)
- [DiskLruCache](https://github.com/JakeWharton/DiskLruCache)
- [SmartRefreshLayout](https://github.com/scwang90/SmartRefreshLayout)
- [Banner](https://github.com/youth5201314/banner)

## 应用截图(点击查看大图)

<p>
<img src="https://github.com/wenhaiz/ListenAll/blob/master/img/main_online.png" alt="主界面" width="270" height="480"/>
<img src="https://github.com/wenhaiz/ListenAll/blob/master/img/main_local.png" alt="主界面" width="270" height="480"/>
<img src="https://github.com/wenhaiz/ListenAll/blob/master/img/new_albums.png" alt="最新音乐" width="270" height="480"/>
</p>
<p>
<img src="https://github.com/wenhaiz/ListenAll/blob/master/img/play.png" alt="播放界面" width="270" height="480"/>
<img src="https://github.com/wenhaiz/ListenAll/blob/master/img/drawer.png" alt="侧滑菜单" width="270" height="480"/>
<img src="https://github.com/wenhaiz/ListenAll/blob/master/img/collect_detail.png" alt="歌单详情" width="270" height="480"/>
</p>
<p>
<img src="https://github.com/wenhaiz/ListenAll/blob/master/img/artist_list.png" alt="艺人列表" width="270" height="480"/>
<img src="https://github.com/wenhaiz/ListenAll/blob/master/img/artist_detail.png" alt="艺人详情" width="270" height="480"/>
<img src="https://github.com/wenhaiz/ListenAll/blob/master/img/collect_tag.png" alt="标签" width="270" height="480"/>
</p>
<p>
<img src="https://github.com/wenhaiz/ListenAll/blob/master/img/collects.png" alt="歌单" width="270" height="480"/>
<img src="https://github.com/wenhaiz/ListenAll/blob/master/img/search.png" alt="搜索" width="270" height="480"/>
<img src="https://github.com/wenhaiz/ListenAll/blob/master/img/ranking_list.png" alt="排行榜" width="270" height="480"/>
</p>

## 更新日志   

### V 1.0.0 Preview *2017-09-14*
- 虾米免费音乐资源
- 歌曲播放和搜索
- 播放历史和搜索历史
- 专辑收藏和歌单收藏
- 虾米排行榜
- 分类歌单
- 艺人列表和详情
- 仅 wifi 联网模式
- 自建歌单





