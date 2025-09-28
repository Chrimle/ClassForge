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
 * The <em>strategy</em> to handle non-null, non-matching <em>value</em> during
 * <strong>Enum</strong> deserialization. The <em>default strategy</em>, with respect to {@link
 * Enum#valueOf(Class, String)}, will {@link #THROW} when there is no match.
 *
 * <p><strong>Use Cases</strong>
 *
 * <ul>
 *   <li><strong>FALLBACK</strong> - when the <em>value</em> may be malformed or unsupported, and
 *       there is a definitive and <em>universal</em> fallback value.
 *   <li><strong>NULL</strong> - when the <em>value</em> may be malformed or unsupported, but
 *       further context is needed to resolve a fallback value.
 *   <li><strong>THROW</strong> - when the <em>value</em> may be malformed or unsupported, and an
 *       Exception shall be thrown immediately.
 * </ul>
 *
 * @since 0.11.0
 * @author Chrimle
 */
@API(status = API.Status.EXPERIMENTAL, since = "0.11.0")
public enum NoMatchStrategy {
  /**
   * This strategy will <em>return</em> a "fallback"/"default" value, if the provided <em>value</em>
   * for deserialization is non-null and non-matching.
   *
   * @since 0.11.0
   */
  @API(status = API.Status.EXPERIMENTAL, since = "0.11.0")
  FALLBACK,
  /**
   * This strategy will <em>return</em> {@code null}, if the provided <em>value</em> for
   * deserialization is non-null and non-matching.
   *
   * @since 0.11.0
   */
  @API(status = API.Status.EXPERIMENTAL, since = "0.11.0")
  NULL,
  /**
   * This strategy will <em>throw</em> an {@link IllegalArgumentException}, if the provided
   * <em>value</em> is non-null and non-matching. This is the <strong>default</strong> strategy,
   * inherited from {@link Enum#valueOf(Class, String)}.
   *
   * @since 0.11.0
   */
  @API(status = API.Status.EXPERIMENTAL, since = "0.11.0")
  THROW
}
