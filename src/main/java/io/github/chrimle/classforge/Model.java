/*
 * Copyright 2025-2026 Chrimle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.chrimle.classforge;

import io.github.chrimle.classforge.classes.ClassModel;
import io.github.chrimle.classforge.enums.EnumModel;
import org.apiguardian.api.API;

/**
 * Abstraction of {@link ClassModel} and {@link EnumModel}.
 *
 * @since 0.11.0
 */
@API(status = API.Status.INTERNAL, since = "0.11.0")
public sealed interface Model permits ClassModel, EnumModel {}
