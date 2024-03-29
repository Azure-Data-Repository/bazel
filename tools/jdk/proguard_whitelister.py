# Copyright 2015 The Bazel Authors. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""Checks for proguard configuration rules that cannot be combined across libs.

The only valid proguard arguments for a library are -keep, -assumenosideeffects,
-assumevalues and -dontnote and -dontwarn when they are provided with arguments.
Limiting libraries to using these flags prevents drastic, sweeping effects
(such as obfuscation being disabled) from being inadvertently applied to a
binary through a library dependency.
"""

import argparse
import re
from typing import Sequence

PROGUARD_COMMENTS_PATTERN = '#.*(\n|$)'


class ProguardConfigValidator(object):
  """Validates a proguard config."""

  # Must be a tuple for str.startswith()
  _VALID_ARGS = ('keep', 'assumenosideeffects', 'assumevalues',
                 'adaptresourcefilecontents', 'if')

  def __init__(self, config_path: str, outconfig_path: str):
    self._config_path = config_path
    self._outconfig_path = outconfig_path

  def ValidateAndWriteOutput(self):
    with open(self._config_path) as config:
      config_string = config.read()
      invalid_configs = self._Validate(config_string)
      if invalid_configs:
        raise RuntimeError(
            'Invalid library proguard config parameters '
            '(these parameters are either invalid or only supported in '
            'android_binary rules): ' + str(invalid_configs))
    with open(self._outconfig_path, 'w+') as outconfig:
      config_string = '# Merged from %s \n%s' % (
          self._config_path, config_string)
      outconfig.write(config_string)

  def _Validate(self, config: str) -> Sequence[str]:
    """Checks the config for illegal arguments."""
    config = re.sub(PROGUARD_COMMENTS_PATTERN, '', config)
    args = re.compile('(?:^-|\n-)').split(config)

    invalid_configs = []
    for arg in args:
      arg = arg.strip()
      if not arg or self._ValidateArg(arg):
        continue
      invalid_configs.append('-' + arg.split()[0])

    return invalid_configs

  def _ValidateArg(self, arg: str) -> bool:
    if arg.startswith(ProguardConfigValidator._VALID_ARGS):
      return True
    elif arg.split()[0] in ['dontnote', 'dontwarn']:
      if len(arg.split()) > 1:
        return True
    return False


def ParseCommandLine() -> Sequence[str]:
  """Parses the command line and returns a flags block."""
  parser = argparse.ArgumentParser(
      description="""Checks for proguard configuration rules that cannot be combined across libs.

The only valid proguard arguments for a library are -keep, -assumenosideeffects,
-assumevalues and -dontnote and -dontwarn when they are provided with arguments.
Limiting libraries to using these flags prevents drastic, sweeping effects
(such as obfuscation being disabled) from being inadvertently applied to a
binary through a library dependency.""")
  parser.add_argument('--path', required=True,
                      help='Path to the proguard config to validate')
  parser.add_argument('--output', required=True,
                      help='Where to put the validated config')
  flags = parser.parse_args()
  return flags


def main():
  flags = ParseCommandLine()
  validator = ProguardConfigValidator(flags.path, flags.output)
  validator.ValidateAndWriteOutput()


if __name__ == '__main__':
  main()
