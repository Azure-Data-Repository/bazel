// Copyright 2016 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.rules.objc;

import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.concurrent.ThreadSafety.Immutable;
import com.google.devtools.build.lib.packages.BuiltinProvider;
import com.google.devtools.build.lib.packages.NativeInfo;
import com.google.devtools.build.lib.rules.cpp.CcInfo;
import com.google.devtools.build.lib.starlarkbuildapi.objc.AppleExecutableBinaryApi;

/**
 * Provider containing the executable binary output that was built using an apple_binary target with
 * the 'executable' type. This provider contains:
 *
 * <ul>
 *   <li>'binary': The executable binary artifact output by apple_binary
 *   <li>'cc_info': A {@link CcInfo} which contains information about the transitive dependencies
 *       linked into the binary.
 * </ul>
 */
@Immutable
public final class AppleExecutableBinaryInfo extends NativeInfo
    implements AppleExecutableBinaryApi {

  /** Starlark name for the AppleExecutableBinaryInfo. */
  public static final String STARLARK_NAME = "AppleExecutableBinary";

  /** Starlark constructor and identifier for AppleExecutableBinaryInfo. */
  public static final BuiltinProvider<AppleExecutableBinaryInfo> STARLARK_CONSTRUCTOR =
      new BuiltinProvider<AppleExecutableBinaryInfo>(
          STARLARK_NAME, AppleExecutableBinaryInfo.class) {};

  private final Artifact appleExecutableBinary;
  private final CcInfo depsCcInfo;

  /**
   * Creates a new AppleExecutableBinaryInfo provider that propagates the given apple_binary
   * configured as an executable.
   */
  public AppleExecutableBinaryInfo(Artifact appleExecutableBinary, CcInfo depsCcInfo) {
    this.appleExecutableBinary = appleExecutableBinary;
    this.depsCcInfo = depsCcInfo;
  }

  @Override
  public BuiltinProvider<AppleExecutableBinaryInfo> getProvider() {
    return STARLARK_CONSTRUCTOR;
  }

  /**
   * Returns the multi-architecture executable binary that apple_binary created.
   */
  @Override
  public Artifact getAppleExecutableBinary() {
    return appleExecutableBinary;
  }

  /**
   * Returns the {@link CcInfo} which contains information about the transitive dependencies linked
   * into the dylib.
   */
  @Override
  public CcInfo getDepsCcInfo() {
    return depsCcInfo;
  }
}
