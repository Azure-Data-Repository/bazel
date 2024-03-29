// Copyright 2015 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.rules.android;

import static com.google.devtools.build.lib.rules.android.AndroidStarlarkData.fromNoneable;

import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.concurrent.ThreadSafety.Immutable;
import com.google.devtools.build.lib.packages.BuiltinProvider;
import com.google.devtools.build.lib.packages.NativeInfo;
import com.google.devtools.build.lib.starlarkbuildapi.android.ApkInfoApi;
import java.util.List;
import javax.annotation.Nullable;
import net.starlark.java.eval.EvalException;
import net.starlark.java.eval.Sequence;

/** A provider for targets that produce an apk file. */
@Immutable
public class ApkInfo extends NativeInfo implements ApkInfoApi<Artifact> {

  private static final String STARLARK_NAME = "ApkInfo";

  /**
   * Provider instance for {@link ApkInfo}.
   */
  public static final ApkInfoProvider PROVIDER = new ApkInfoProvider();

  private final Artifact apk;
  private final Artifact unsignedApk;
  private final Artifact deployJar;
  @Nullable private final Artifact coverageMetadata;
  private final Artifact mergedManifest;
  private final ImmutableList<Artifact> signingKeys;
  @Nullable private final Artifact signingLineage;
  @Nullable private final String signingMinV3RotationApiVersion;

  ApkInfo(
      Artifact apk,
      Artifact unsignedApk,
      Artifact deployJar,
      @Nullable Artifact coverageMetadata,
      Artifact mergedManifest,
      List<Artifact> signingKeys,
      @Nullable Artifact signingLineage,
      @Nullable String signingMinV3RotationApiVersion) {
    this.apk = apk;
    this.unsignedApk = unsignedApk;
    this.deployJar = deployJar;
    this.coverageMetadata = coverageMetadata;
    this.mergedManifest = mergedManifest;
    this.signingKeys = ImmutableList.copyOf(signingKeys);
    this.signingLineage = signingLineage;
    this.signingMinV3RotationApiVersion = signingMinV3RotationApiVersion;
  }

  @Override
  public ApkInfoProvider getProvider() {
    return PROVIDER;
  }

  @Override
  public Artifact getApk() {
    return apk;
  }

  /** Returns the unsigned APK file built in the transitive closure. */
  @Override
  public Artifact getUnsignedApk() {
    return unsignedApk;
  }

  /** Returns the deploy jar used to build the APK. */
  @Override
  public Artifact getDeployJar() {
    return deployJar;
  }

  /** Returns the coverage metadata artifact generated in the transitive closure. */
  @Nullable
  @Override
  public Artifact getCoverageMetadata() {
    return coverageMetadata;
  }

  /** Returns the merged manifest. */
  public Artifact getMergedManifest() {
    return mergedManifest;
  }

  /* The keystore that was used to sign the apk returned from {@see getApk() */
  @Override
  public Artifact getKeystore() {
    return signingKeys.get(0);
  }

  @Override
  public ImmutableList<Artifact> getSigningKeys() {
    return signingKeys;
  }

  @Nullable
  @Override
  public Artifact getSigningLineage() {
    return signingLineage;
  }

  @Nullable
  @Override
  public String getSigningMinV3RotationApiVersion() {
    return signingMinV3RotationApiVersion;
  }

  /** Provider for {@link ApkInfo}. */
  public static class ApkInfoProvider extends BuiltinProvider<ApkInfo>
      implements ApkInfoApiProvider<Artifact> {

    private ApkInfoProvider() {
      super(STARLARK_NAME, ApkInfo.class);
    }

    @Override
    public ApkInfoApi<Artifact> createInfo(
        Artifact apk,
        Artifact unsignedApk,
        Artifact deployJar,
        Object coverageMetadata,
        Artifact mergedManifest,
        Sequence<?> signingKeys,
        Object signingLineage,
        Object signingMinV3RotationApiVersion)
        throws EvalException {
      return new ApkInfo(
          apk,
          unsignedApk,
          deployJar,
          fromNoneable(coverageMetadata, Artifact.class),
          mergedManifest,
          Sequence.cast(signingKeys, Artifact.class, "signing_keys"),
          fromNoneable(signingLineage, Artifact.class),
          fromNoneable(signingMinV3RotationApiVersion, String.class));
    }
  }
}
