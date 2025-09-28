/*
 * Copyright 2025 Chrimle
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
package io.github.chrimle.classforge.enums;

import org.apiguardian.api.API;

/**
 * The <em>strategy</em> to handle value-matching during <strong>Enum</strong> deserialization. The
 * <em>default strategy</em>, with respect to {@link Enum#valueOf(Class, String)}, will match by
 * {@link #EXACT_NAME}.
 *
 * <p><strong>Use Cases</strong>
 *
 * <ul>
 *   <li><strong>CASE_INSENSITIVE_NAME</strong> - when the value is <em>not required</em> to be in
 *       the exact same case as the <strong>name</strong> of the enum constant.
 *   <li><strong>EXACT_NAME</strong> - when the value <strong>must</strong> be in the exact same
 *       case as the <strong>name</strong> of the enum constant.
 * </ul>
 *
 * @since 0.11.0
 * @author Chrimle
 */
@API(status = API.Status.EXPERIMENTAL, since = "0.11.0")
public enum MatchingStrategy {
  /**
   * This strategy will try to match the provided <em>value</em> <strong>case insensitively</strong>
   * to the <strong>name</strong> of an enum constant. E.g. {@code "ExAmPlE"} will match with both
   * {@code EXAMPLE} and {@code example}. <strong>NOTE:</strong> In case of an enum class having
   * multiple constants with the same name, <em>with varying cases</em>, the provided value will be
   * matched to the <strong>first matching</strong> constant by the order they are
   * <strong>declared</strong> in the enum class.
   *
   * @since 0.11.0
   */
  @API(status = API.Status.EXPERIMENTAL, since = "0.11.0")
  CASE_INSENSITIVE_NAME,
  /**
   * This strategy will try to match the provided <em>value</em> <strong>exactly</strong> to the
   * <strong>name</strong> of an enum constant. This is the <strong>default</strong> strategy,
   * inherited from {@link Enum#valueOf(Class, String)}.
   *
   * @since 0.11.0
   */
  @API(status = API.Status.EXPERIMENTAL, since = "0.11.0")
  EXACT_NAME
}
