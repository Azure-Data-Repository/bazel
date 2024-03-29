// Copyright 2023 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.skyframe.serialization;

import static com.google.common.truth.Truth.assertThat;

import com.google.devtools.build.lib.skyframe.serialization.testutils.SerializationTester;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for {@link AtomicReferenceArrayCodec}. */
@RunWith(JUnit4.class)
public final class AtomicReferenceArrayCodecTest {

  @Test
  public void arrays() throws Exception {
    var instance1 = new AtomicReferenceArray<Integer>(3);
    instance1.setPlain(0, 0);
    instance1.setPlain(1, 1);
    instance1.setPlain(2, null);
    var instance2 = new AtomicReferenceArray<String>(3);
    instance2.setPlain(0, "foo");
    instance2.setPlain(1, null);
    instance2.setPlain(2, "bar");
    new SerializationTester(new AtomicReferenceArray<Object>(0), instance1, instance2)
        .setVerificationFunction(AtomicReferenceArrayCodecTest::verifyDeserialized)
        .runTests();
  }

  private static void verifyDeserialized(
      AtomicReferenceArray<?> original, AtomicReferenceArray<?> deserialized) {
    assertThat(deserialized.length()).isEqualTo(original.length());
    for (int i = 0; i < deserialized.length(); i++) {
      assertThat(deserialized.getPlain(i)).isEqualTo(original.getPlain(i));
    }
  }
}
