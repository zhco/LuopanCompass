# 风水罗盘 (Luopan Compass)

一款专为地理先生设计的中国传统风水罗盘 Android 应用。

## 功能特性

- **真实指南针** - 利用手机传感器实时显示朝向
- **二十四山盘** - 天盘与地盘双层二十四山显示
- **八卦方位** - 内圈八卦标识
- **坐山朝向** - 实时显示当前方位对应的山水
- **GPS定位** - 显示当前经纬度坐标
- **传统风格** - 仿古木纹金色罗盘设计

## 二十四山

罗盘包含完整的二十四山方位：
- **四正**: 子(北)、卯(东)、午(南)、酉(西)
- **四隅**: 乾(西北)、艮(东北)、巽(东南)、坤(西南)
- **十二支**: 子丑寅卯辰巳午未申酉戌亥
- **八干**: 甲乙丙丁庚辛壬癸
- **四维**: 乾坤艮巽

## 技术栈

- Kotlin
- Android SDK 34
- 自定义 Canvas 绘制
- SensorManager (加速度计 + 磁力计)
- GPS LocationManager

## 编译

```bash
# 使用 Gradle 编译
./gradlew assembleRelease

# APK 输出路径
# app/build/outputs/apk/release/app-release.apk
```

## GitHub Actions

本项目配置了 GitHub Actions 自动编译，每次推送代码到 main 分支时会自动：
1. 编译 Release APK
2. 上传 APK 到 Artifacts
3. 创建 GitHub Release

## 使用说明

1. 安装 APK 到 Android 手机
2. 授予位置权限
3. 将手机水平放置
4. 缓慢旋转手机校准罗盘
5. 罗盘会实时显示当前方位

## 许可证

MIT License
