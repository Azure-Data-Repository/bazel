load("@rules_python//python:defs.bzl", "py_library")

licenses(["notice"])

filegroup(
    name = "srcs",
    srcs = glob(["**"]),
    visibility = ["//third_party:__pkg__"],
)

py_library(
    name = "mock",
    srcs = ["__init__.py"],
    visibility = ["//visibility:public"],
)
