// Copyright 2018 The Bazel Authors. All rights reserved.
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

import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.skyframe.serialization.testutils.SerializationTester;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for {@link MethodCodec}. */
@RunWith(JUnit4.class)
public class MethodCodecTest {
  @Test
  public void smoke() throws Exception {
    var methods = new ImmutableList.Builder<Method>();
    getMethods(String.class, methods);
    getMethods(List.class, methods);
    new SerializationTester(methods.build()).runTests();
  }

  private static void getMethods(Class<?> clazz, ImmutableList.Builder<Method> out)
      throws NoSuchMethodException {
    for (var method : clazz.getMethods()) {
      Class<?> declaringClass = method.getDeclaringClass();
      out.add(declaringClass.getDeclaredMethod(method.getName(), method.getParameterTypes()));
    }
  }
}
