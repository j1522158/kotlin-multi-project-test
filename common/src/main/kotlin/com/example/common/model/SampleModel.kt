package com.example.common.model

/**
 * アノテーション一覧
 * @SerializedName: JSONシリアライズ/デシリアライズ時にプロパティ名を指定する
 * @JsonProperty: Jacksonや他のJSONライブラリで使用する
 * @JvmField: プロパティをJavaから直接アクセス可能にする
 * @Ignore: 特定のフィールドを無視するために使用する(ORM用）
 * @Transient: シリアライズからプロパティを除外する
 */

data class SampleModel(
    var sampleStr: String,
    val sampleNumber: Long
)