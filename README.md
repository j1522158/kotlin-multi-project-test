# kotlin-gradle-multi-project
Kotlin x SpringBootでのGradleマルチプロジェクトサンプル
 - 1つのリポジトリにapi/batchを集約する
 - 共通処理をcommonとして切り出す

## バージョン
| 名称         | バージョン |
|------------|-------|
| OpenJDK    | 17    |
| Kotlin     | 2.0   |
| SpringBoot | 3.3   |
| Gradle     | 8.10  |

## ディレクトリ構造
```
kotlin-multi-project-test
├── common
│   └── // 共通処理を配置するためのサブプロジェクト
├── api
│   └── // SpringBootで作成したRestAPI
└── batch
    └── // SpringBootで作成したBatch
```