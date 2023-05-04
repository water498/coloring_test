package com.example.coloringtest.model

import java.nio.file.Path

// Path = 페인트로 색칠, 펜으로 색칠 다 들어감 -> paint 정보도 같이 추가해주면 될듯
// 스티커도 추가 가능할 듯
data class Action(val path: Path, val color: Int , val strokeWidth : Int)