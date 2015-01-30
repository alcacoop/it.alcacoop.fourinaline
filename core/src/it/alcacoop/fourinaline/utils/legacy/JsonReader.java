// line 1 "JsonReader.rl"
// Do not edit this file! Generated by Ragel.
// Ragel.exe -G2 -J -o JsonReader.java JsonReader.rl
/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
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
 ******************************************************************************/

package it.alcacoop.fourinaline.utils.legacy;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.SerializationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/** Lightweight JSON parser.<br>
 * <br>
 * The default behavior is to parse the JSON into a DOM made up of {@link OrderedMap}, {@link Array}, String, Float, and Boolean
 * objects. Extend this class and override methods to perform event driven parsing. When this is done, the parse methods will
 * return null.
 * @author Nathan Sweet */
public class JsonReader {
	public Object parse (String json) {
		char[] data = json.toCharArray();
		return parse(data, 0, data.length);
	}

	public Object parse (Reader reader) {
		try {
			char[] data = new char[1024];
			int offset = 0;
			while (true) {
				int length = reader.read(data, offset, data.length - offset);
				if (length == -1) break;
				if (length == 0) {
					char[] newData = new char[data.length * 2];
					System.arraycopy(data, 0, newData, 0, data.length);
					data = newData;
				} else
					offset += length;
			}
			return parse(data, 0, offset);
		} catch (IOException ex) {
			throw new SerializationException(ex);
		} finally {
			try {
				reader.close();
			} catch (IOException ignored) {
			}
		}
	}

	public Object parse (InputStream input) {
		try {
			return parse(new InputStreamReader(input, "ISO-8859-1"));
		} catch (IOException ex) {
			throw new SerializationException(ex);
		}
	}

	public Object parse (FileHandle file) {
		try {
			return parse(file.read());
		} catch (Exception ex) {
			throw new SerializationException("Error parsing file: " + file, ex);
		}
	}

	public Object parse (char[] data, int offset, int length) {
		int cs, p = offset, pe = length, eof = pe, top = 0;
		int[] stack = new int[4];

		int s = 0;
		Array<String> names = new Array(8);
		boolean needsUnescape = false;
		boolean discardBuffer = false; // When unquotedString and true/false/null both match, this discards unquotedString.
		RuntimeException parseRuntimeEx = null;

		boolean debug = false;
		if (debug) System.out.println();

		try {

			// line 3 "JsonReader.java"
			{
				cs = json_start;
				top = 0;
			}

			// line 8 "JsonReader.java"
			{
				int _klen;
				int _trans = 0;
				int _acts;
				int _nacts;
				int _keys;
				int _goto_targ = 0;

				_goto:
				while (true) {
					switch (_goto_targ) {
					case 0:
						if (p == pe) {
							_goto_targ = 4;
							continue _goto;
						}
						if (cs == 0) {
							_goto_targ = 5;
							continue _goto;
						}
					case 1:
						_match:
						do {
							_keys = _json_key_offsets[cs];
							_trans = _json_index_offsets[cs];
							_klen = _json_single_lengths[cs];
							if (_klen > 0) {
								int _lower = _keys;
								int _mid;
								int _upper = _keys + _klen - 1;
								while (true) {
									if (_upper < _lower) break;

									_mid = _lower + ((_upper - _lower) >> 1);
									if (data[p] < _json_trans_keys[_mid])
										_upper = _mid - 1;
									else if (data[p] > _json_trans_keys[_mid])
										_lower = _mid + 1;
									else {
										_trans += (_mid - _keys);
										break _match;
									}
								}
								_keys += _klen;
								_trans += _klen;
							}

							_klen = _json_range_lengths[cs];
							if (_klen > 0) {
								int _lower = _keys;
								int _mid;
								int _upper = _keys + (_klen << 1) - 2;
								while (true) {
									if (_upper < _lower) break;

									_mid = _lower + (((_upper - _lower) >> 1) & ~1);
									if (data[p] < _json_trans_keys[_mid])
										_upper = _mid - 2;
									else if (data[p] > _json_trans_keys[_mid + 1])
										_lower = _mid + 2;
									else {
										_trans += ((_mid - _keys) >> 1);
										break _match;
									}
								}
								_trans += _klen;
							}
						} while (false);

						cs = _json_trans_targs[_trans];

						if (_json_trans_actions[_trans] != 0) {
							_acts = _json_trans_actions[_trans];
							_nacts = (int)_json_actions[_acts++];
							while (_nacts-- > 0) {
								switch (_json_actions[_acts++]) {
								case 0:
								// line 105 "JsonReader.rl"
								{
									s = p;
									needsUnescape = false;
									discardBuffer = false;
								}
									break;
								case 1:
								// line 110 "JsonReader.rl"
								{
									needsUnescape = true;
								}
									break;
								case 2:
								// line 113 "JsonReader.rl"
								{
									String name = new String(data, s, p - s);
									s = p;
									if (needsUnescape) name = unescape(name);
									if (debug) System.out.println("name: " + name);
									names.add(name);
								}
									break;
								case 3:
								// line 120 "JsonReader.rl"
								{
									if (!discardBuffer) {
										String value = new String(data, s, p - s);
										s = p;
										if (needsUnescape) value = unescape(value);
										String name = names.size > 0 ? names.pop() : null;
										if (debug) System.out.println("string: " + name + "=" + value);
										string(name, value);
									}
								}
									break;
								case 4:
								// line 130 "JsonReader.rl"
								{
									String value = new String(data, s, p - s);
									s = p;
									String name = names.size > 0 ? names.pop() : null;
									if (debug) System.out.println("number: " + name + "=" + Float.parseFloat(value));
									number(name, Float.parseFloat(value));
								}
									break;
								case 5:
								// line 137 "JsonReader.rl"
								{
									String name = names.size > 0 ? names.pop() : null;
									if (debug) System.out.println("boolean: " + name + "=true");
									bool(name, true);
									discardBuffer = true;
								}
									break;
								case 6:
								// line 143 "JsonReader.rl"
								{
									String name = names.size > 0 ? names.pop() : null;
									if (debug) System.out.println("boolean: " + name + "=false");
									bool(name, false);
									discardBuffer = true;
								}
									break;
								case 7:
								// line 149 "JsonReader.rl"
								{
									String name = names.size > 0 ? names.pop() : null;
									if (debug) System.out.println("null: " + name);
									string(name, null);
									discardBuffer = true;
								}
									break;
								case 8:
								// line 155 "JsonReader.rl"
								{
									String name = names.size > 0 ? names.pop() : null;
									if (debug) System.out.println("startObject: " + name);
									startObject(name);
									{
										if (top == stack.length) {
											int[] newStack = new int[stack.length * 2];
											System.arraycopy(stack, 0, newStack, 0, stack.length);
											stack = newStack;
										}
										{
											stack[top++] = cs;
											cs = 9;
											_goto_targ = 2;
											if (true) continue _goto;
										}
									}
								}
									break;
								case 9:
								// line 161 "JsonReader.rl"
								{
									if (debug) System.out.println("endObject");
									pop();
									{
										cs = stack[--top];
										_goto_targ = 2;
										if (true) continue _goto;
									}
								}
									break;
								case 10:
								// line 166 "JsonReader.rl"
								{
									String name = names.size > 0 ? names.pop() : null;
									if (debug) System.out.println("startArray: " + name);
									startArray(name);
									{
										if (top == stack.length) {
											int[] newStack = new int[stack.length * 2];
											System.arraycopy(stack, 0, newStack, 0, stack.length);
											stack = newStack;
										}
										{
											stack[top++] = cs;
											cs = 49;
											_goto_targ = 2;
											if (true) continue _goto;
										}
									}
								}
									break;
								case 11:
								// line 172 "JsonReader.rl"
								{
									if (debug) System.out.println("endArray");
									pop();
									{
										cs = stack[--top];
										_goto_targ = 2;
										if (true) continue _goto;
									}
								}
									break;
								// line 207 "JsonReader.java"
								}
							}
						}

					case 2:
						if (cs == 0) {
							_goto_targ = 5;
							continue _goto;
						}
						if (++p != pe) {
							_goto_targ = 1;
							continue _goto;
						}
					case 4:
						if (p == eof) {
							int __acts = _json_eof_actions[cs];
							int __nacts = (int)_json_actions[__acts++];
							while (__nacts-- > 0) {
								switch (_json_actions[__acts++]) {
								case 3:
								// line 120 "JsonReader.rl"
								{
									if (!discardBuffer) {
										String value = new String(data, s, p - s);
										s = p;
										if (needsUnescape) value = unescape(value);
										String name = names.size > 0 ? names.pop() : null;
										if (debug) System.out.println("string: " + name + "=" + value);
										string(name, value);
									}
								}
									break;
								case 4:
								// line 130 "JsonReader.rl"
								{
									String value = new String(data, s, p - s);
									s = p;
									String name = names.size > 0 ? names.pop() : null;
									if (debug) System.out.println("number: " + name + "=" + Float.parseFloat(value));
									number(name, Float.parseFloat(value));
								}
									break;
								case 5:
								// line 137 "JsonReader.rl"
								{
									String name = names.size > 0 ? names.pop() : null;
									if (debug) System.out.println("boolean: " + name + "=true");
									bool(name, true);
									discardBuffer = true;
								}
									break;
								case 6:
								// line 143 "JsonReader.rl"
								{
									String name = names.size > 0 ? names.pop() : null;
									if (debug) System.out.println("boolean: " + name + "=false");
									bool(name, false);
									discardBuffer = true;
								}
									break;
								case 7:
								// line 149 "JsonReader.rl"
								{
									String name = names.size > 0 ? names.pop() : null;
									if (debug) System.out.println("null: " + name);
									string(name, null);
									discardBuffer = true;
								}
									break;
								// line 278 "JsonReader.java"
								}
							}
						}

					case 5:
					}
					break;
				}
			}

			// line 203 "JsonReader.rl"

		} catch (RuntimeException ex) {
			parseRuntimeEx = ex;
		}

		if (p < pe) {
			int lineNumber = 1;
			for (int i = 0; i < p; i++)
				if (data[i] == '\n') lineNumber++;
			throw new SerializationException("Error parsing JSON on line " + lineNumber + " near: " + new String(data, p, pe - p),
				parseRuntimeEx);
		} else if (elements.size != 0) {
			Object element = elements.peek();
			elements.clear();
			if (element instanceof OrderedMap)
				throw new SerializationException("Error parsing JSON, unmatched brace.");
			else
				throw new SerializationException("Error parsing JSON, unmatched bracket.");
		}
		Object root = this.root;
		this.root = null;
		return root;
	}

	// line 288 "JsonReader.java"
	private static byte[] init__json_actions_0 () {
		return new byte[] {0, 1, 0, 1, 1, 1, 2, 1, 3, 1, 4, 1, 8, 1, 9, 1, 10, 1, 11, 2, 0, 2, 2, 0, 3, 2, 3, 9, 2, 3, 11, 2, 4, 9,
			2, 4, 11, 2, 5, 3, 2, 6, 3, 2, 7, 3, 3, 5, 3, 9, 3, 5, 3, 11, 3, 6, 3, 9, 3, 6, 3, 11, 3, 7, 3, 9, 3, 7, 3, 11};
	}

	private static final byte _json_actions[] = init__json_actions_0();

	private static short[] init__json_key_offsets_0 () {
		return new short[] {0, 0, 18, 20, 22, 31, 33, 35, 39, 41, 56, 58, 60, 64, 82, 84, 86, 91, 105, 112, 114, 123, 125, 133,
			137, 139, 145, 154, 161, 163, 173, 175, 184, 188, 190, 197, 205, 213, 221, 229, 236, 244, 252, 260, 267, 275, 283, 291,
			298, 307, 327, 329, 331, 336, 355, 362, 364, 374, 376, 385, 389, 391, 398, 406, 414, 422, 430, 437, 445, 453, 461, 468,
			476, 484, 492, 499, 508, 511, 518, 526, 533, 538, 546, 554, 562, 570, 577, 585, 593, 601, 608, 616, 624, 632, 639, 639};
	}

	private static final short _json_key_offsets[] = init__json_key_offsets_0();

	private static char[] init__json_trans_keys_0 () {
		return new char[] {32, 34, 36, 45, 91, 95, 102, 110, 116, 123, 9, 13, 48, 57, 65, 90, 97, 122, 34, 92, 34, 92, 34, 47, 92,
			98, 102, 110, 114, 116, 117, 48, 57, 48, 57, 43, 45, 48, 57, 48, 57, 32, 34, 36, 44, 45, 95, 125, 9, 13, 48, 57, 65, 90,
			97, 122, 34, 92, 34, 92, 32, 58, 9, 13, 32, 34, 36, 45, 91, 95, 102, 110, 116, 123, 9, 13, 48, 57, 65, 90, 97, 122, 34,
			92, 34, 92, 32, 44, 125, 9, 13, 32, 34, 36, 45, 95, 125, 9, 13, 48, 57, 65, 90, 97, 122, 32, 44, 58, 93, 125, 9, 13, 48,
			57, 32, 46, 58, 69, 101, 9, 13, 48, 57, 48, 57, 32, 58, 69, 101, 9, 13, 48, 57, 43, 45, 48, 57, 48, 57, 32, 58, 9, 13,
			48, 57, 34, 47, 92, 98, 102, 110, 114, 116, 117, 32, 44, 58, 93, 125, 9, 13, 48, 57, 32, 44, 46, 69, 101, 125, 9, 13,
			48, 57, 48, 57, 32, 44, 69, 101, 125, 9, 13, 48, 57, 43, 45, 48, 57, 48, 57, 32, 44, 125, 9, 13, 48, 57, 32, 44, 58, 93,
			97, 125, 9, 13, 32, 44, 58, 93, 108, 125, 9, 13, 32, 44, 58, 93, 115, 125, 9, 13, 32, 44, 58, 93, 101, 125, 9, 13, 32,
			44, 58, 93, 125, 9, 13, 32, 44, 58, 93, 117, 125, 9, 13, 32, 44, 58, 93, 108, 125, 9, 13, 32, 44, 58, 93, 108, 125, 9,
			13, 32, 44, 58, 93, 125, 9, 13, 32, 44, 58, 93, 114, 125, 9, 13, 32, 44, 58, 93, 117, 125, 9, 13, 32, 44, 58, 93, 101,
			125, 9, 13, 32, 44, 58, 93, 125, 9, 13, 34, 47, 92, 98, 102, 110, 114, 116, 117, 32, 34, 36, 44, 45, 91, 93, 95, 102,
			110, 116, 123, 9, 13, 48, 57, 65, 90, 97, 122, 34, 92, 34, 92, 32, 44, 93, 9, 13, 32, 34, 36, 45, 91, 93, 95, 102, 110,
			116, 123, 9, 13, 48, 57, 65, 90, 97, 122, 32, 44, 58, 93, 125, 9, 13, 48, 57, 32, 44, 46, 69, 93, 101, 9, 13, 48, 57,
			48, 57, 32, 44, 69, 93, 101, 9, 13, 48, 57, 43, 45, 48, 57, 48, 57, 32, 44, 93, 9, 13, 48, 57, 32, 44, 58, 93, 97, 125,
			9, 13, 32, 44, 58, 93, 108, 125, 9, 13, 32, 44, 58, 93, 115, 125, 9, 13, 32, 44, 58, 93, 101, 125, 9, 13, 32, 44, 58,
			93, 125, 9, 13, 32, 44, 58, 93, 117, 125, 9, 13, 32, 44, 58, 93, 108, 125, 9, 13, 32, 44, 58, 93, 108, 125, 9, 13, 32,
			44, 58, 93, 125, 9, 13, 32, 44, 58, 93, 114, 125, 9, 13, 32, 44, 58, 93, 117, 125, 9, 13, 32, 44, 58, 93, 101, 125, 9,
			13, 32, 44, 58, 93, 125, 9, 13, 34, 47, 92, 98, 102, 110, 114, 116, 117, 32, 9, 13, 32, 44, 58, 93, 125, 9, 13, 32, 46,
			69, 101, 9, 13, 48, 57, 32, 69, 101, 9, 13, 48, 57, 32, 9, 13, 48, 57, 32, 44, 58, 93, 97, 125, 9, 13, 32, 44, 58, 93,
			108, 125, 9, 13, 32, 44, 58, 93, 115, 125, 9, 13, 32, 44, 58, 93, 101, 125, 9, 13, 32, 44, 58, 93, 125, 9, 13, 32, 44,
			58, 93, 117, 125, 9, 13, 32, 44, 58, 93, 108, 125, 9, 13, 32, 44, 58, 93, 108, 125, 9, 13, 32, 44, 58, 93, 125, 9, 13,
			32, 44, 58, 93, 114, 125, 9, 13, 32, 44, 58, 93, 117, 125, 9, 13, 32, 44, 58, 93, 101, 125, 9, 13, 32, 44, 58, 93, 125,
			9, 13, 0};
	}

	private static final char _json_trans_keys[] = init__json_trans_keys_0();

	private static byte[] init__json_single_lengths_0 () {
		return new byte[] {0, 10, 2, 2, 7, 0, 0, 2, 0, 7, 2, 2, 2, 10, 2, 2, 3, 6, 5, 0, 5, 0, 4, 2, 0, 2, 7, 5, 0, 6, 0, 5, 2, 0,
			3, 6, 6, 6, 6, 5, 6, 6, 6, 5, 6, 6, 6, 5, 7, 12, 2, 2, 3, 11, 5, 0, 6, 0, 5, 2, 0, 3, 6, 6, 6, 6, 5, 6, 6, 6, 5, 6, 6,
			6, 5, 7, 1, 5, 4, 3, 1, 6, 6, 6, 6, 5, 6, 6, 6, 5, 6, 6, 6, 5, 0, 0};
	}

	private static final byte _json_single_lengths[] = init__json_single_lengths_0();

	private static byte[] init__json_range_lengths_0 () {
		return new byte[] {0, 4, 0, 0, 1, 1, 1, 1, 1, 4, 0, 0, 1, 4, 0, 0, 1, 4, 1, 1, 2, 1, 2, 1, 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 2,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 0, 0, 1, 4, 1, 1, 2, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0};
	}

	private static final byte _json_range_lengths[] = init__json_range_lengths_0();

	private static short[] init__json_index_offsets_0 () {
		return new short[] {0, 0, 15, 18, 21, 30, 32, 34, 38, 40, 52, 55, 58, 62, 77, 80, 83, 88, 99, 106, 108, 116, 118, 125, 129,
			131, 136, 145, 152, 154, 163, 165, 173, 177, 179, 185, 193, 201, 209, 217, 224, 232, 240, 248, 255, 263, 271, 279, 286,
			295, 312, 315, 318, 323, 339, 346, 348, 357, 359, 367, 371, 373, 379, 387, 395, 403, 411, 418, 426, 434, 442, 449, 457,
			465, 473, 480, 489, 492, 499, 506, 512, 516, 524, 532, 540, 548, 555, 563, 571, 579, 586, 594, 602, 610, 617, 618};
	}

	private static final short _json_index_offsets[] = init__json_index_offsets_0();

	private static byte[] init__json_trans_targs_0 () {
		return new byte[] {1, 2, 77, 5, 76, 77, 81, 86, 90, 76, 1, 78, 77, 77, 0, 76, 4, 3, 76, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0,
			78, 0, 79, 0, 8, 8, 80, 0, 80, 0, 9, 10, 18, 17, 19, 18, 94, 9, 18, 18, 18, 0, 12, 48, 11, 12, 48, 11, 12, 13, 12, 0,
			13, 14, 27, 28, 16, 27, 35, 40, 44, 16, 13, 29, 27, 27, 0, 16, 26, 15, 16, 26, 15, 16, 17, 94, 16, 0, 17, 10, 18, 19,
			18, 94, 17, 18, 18, 18, 0, 12, 0, 13, 0, 0, 12, 18, 20, 0, 12, 21, 13, 23, 23, 12, 20, 0, 22, 0, 12, 13, 23, 23, 12, 22,
			0, 24, 24, 25, 0, 25, 0, 12, 13, 12, 25, 0, 15, 15, 15, 15, 15, 15, 15, 15, 0, 16, 17, 0, 0, 94, 16, 27, 29, 0, 16, 17,
			30, 32, 32, 94, 16, 29, 0, 31, 0, 16, 17, 32, 32, 94, 16, 31, 0, 33, 33, 34, 0, 34, 0, 16, 17, 94, 16, 34, 0, 16, 17, 0,
			0, 36, 94, 16, 27, 16, 17, 0, 0, 37, 94, 16, 27, 16, 17, 0, 0, 38, 94, 16, 27, 16, 17, 0, 0, 39, 94, 16, 27, 16, 17, 0,
			0, 94, 16, 27, 16, 17, 0, 0, 41, 94, 16, 27, 16, 17, 0, 0, 42, 94, 16, 27, 16, 17, 0, 0, 43, 94, 16, 27, 16, 17, 0, 0,
			94, 16, 27, 16, 17, 0, 0, 45, 94, 16, 27, 16, 17, 0, 0, 46, 94, 16, 27, 16, 17, 0, 0, 47, 94, 16, 27, 16, 17, 0, 0, 94,
			16, 27, 11, 11, 11, 11, 11, 11, 11, 11, 0, 49, 50, 54, 53, 55, 52, 95, 54, 62, 67, 71, 52, 49, 56, 54, 54, 0, 52, 75,
			51, 52, 75, 51, 52, 53, 95, 52, 0, 53, 50, 54, 55, 52, 95, 54, 62, 67, 71, 52, 53, 56, 54, 54, 0, 52, 53, 0, 95, 0, 52,
			54, 56, 0, 52, 53, 57, 59, 95, 59, 52, 56, 0, 58, 0, 52, 53, 59, 95, 59, 52, 58, 0, 60, 60, 61, 0, 61, 0, 52, 53, 95,
			52, 61, 0, 52, 53, 0, 95, 63, 0, 52, 54, 52, 53, 0, 95, 64, 0, 52, 54, 52, 53, 0, 95, 65, 0, 52, 54, 52, 53, 0, 95, 66,
			0, 52, 54, 52, 53, 0, 95, 0, 52, 54, 52, 53, 0, 95, 68, 0, 52, 54, 52, 53, 0, 95, 69, 0, 52, 54, 52, 53, 0, 95, 70, 0,
			52, 54, 52, 53, 0, 95, 0, 52, 54, 52, 53, 0, 95, 72, 0, 52, 54, 52, 53, 0, 95, 73, 0, 52, 54, 52, 53, 0, 95, 74, 0, 52,
			54, 52, 53, 0, 95, 0, 52, 54, 51, 51, 51, 51, 51, 51, 51, 51, 0, 76, 76, 0, 76, 0, 0, 0, 0, 76, 77, 76, 6, 7, 7, 76, 78,
			0, 76, 7, 7, 76, 79, 0, 76, 76, 80, 0, 76, 0, 0, 0, 82, 0, 76, 77, 76, 0, 0, 0, 83, 0, 76, 77, 76, 0, 0, 0, 84, 0, 76,
			77, 76, 0, 0, 0, 85, 0, 76, 77, 76, 0, 0, 0, 0, 76, 77, 76, 0, 0, 0, 87, 0, 76, 77, 76, 0, 0, 0, 88, 0, 76, 77, 76, 0,
			0, 0, 89, 0, 76, 77, 76, 0, 0, 0, 0, 76, 77, 76, 0, 0, 0, 91, 0, 76, 77, 76, 0, 0, 0, 92, 0, 76, 77, 76, 0, 0, 0, 93, 0,
			76, 77, 76, 0, 0, 0, 0, 76, 77, 0, 0, 0};
	}

	private static final byte _json_trans_targs[] = init__json_trans_targs_0();

	private static byte[] init__json_trans_actions_0 () {
		return new byte[] {0, 0, 1, 1, 15, 1, 1, 1, 1, 11, 0, 1, 1, 1, 0, 22, 1, 1, 7, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 13, 0, 1, 1, 1, 0, 19, 1, 1, 5, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 15, 1, 1, 1, 1, 11, 0,
			1, 1, 1, 0, 22, 1, 1, 7, 0, 0, 0, 0, 13, 0, 0, 0, 0, 1, 1, 1, 13, 0, 1, 1, 1, 0, 5, 0, 5, 0, 0, 5, 0, 0, 0, 5, 0, 5, 0,
			0, 5, 0, 0, 0, 0, 5, 5, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 7, 7, 0, 0, 25, 7, 0,
			0, 0, 9, 9, 0, 0, 0, 31, 9, 0, 0, 0, 0, 9, 9, 0, 0, 31, 9, 0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 31, 9, 0, 0, 7, 7, 0, 0, 0, 25,
			7, 0, 7, 7, 0, 0, 0, 25, 7, 0, 7, 7, 0, 0, 0, 25, 7, 0, 7, 7, 0, 0, 0, 25, 7, 0, 40, 40, 0, 0, 54, 40, 0, 7, 7, 0, 0, 0,
			25, 7, 0, 7, 7, 0, 0, 0, 25, 7, 0, 7, 7, 0, 0, 0, 25, 7, 0, 43, 43, 0, 0, 62, 43, 0, 7, 7, 0, 0, 0, 25, 7, 0, 7, 7, 0,
			0, 0, 25, 7, 0, 7, 7, 0, 0, 0, 25, 7, 0, 37, 37, 0, 0, 46, 37, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 1, 0, 1, 15, 17, 1,
			1, 1, 1, 11, 0, 1, 1, 1, 0, 22, 1, 1, 7, 0, 0, 0, 0, 17, 0, 0, 0, 0, 1, 1, 15, 17, 1, 1, 1, 1, 11, 0, 1, 1, 1, 0, 7, 7,
			0, 28, 0, 7, 0, 0, 0, 9, 9, 0, 0, 34, 0, 9, 0, 0, 0, 0, 9, 9, 0, 34, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 34, 9, 0, 0, 7,
			7, 0, 28, 0, 0, 7, 0, 7, 7, 0, 28, 0, 0, 7, 0, 7, 7, 0, 28, 0, 0, 7, 0, 7, 7, 0, 28, 0, 0, 7, 0, 40, 40, 0, 58, 0, 40,
			0, 7, 7, 0, 28, 0, 0, 7, 0, 7, 7, 0, 28, 0, 0, 7, 0, 7, 7, 0, 28, 0, 0, 7, 0, 43, 43, 0, 66, 0, 43, 0, 7, 7, 0, 28, 0,
			0, 7, 0, 7, 7, 0, 28, 0, 0, 7, 0, 7, 7, 0, 28, 0, 0, 7, 0, 37, 37, 0, 50, 0, 37, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0,
			7, 0, 0, 0, 0, 7, 0, 9, 0, 0, 0, 9, 0, 0, 9, 0, 0, 9, 0, 0, 9, 9, 0, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0,
			7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0, 40, 0, 0, 0, 0, 40, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0,
			7, 0, 0, 0, 0, 0, 7, 0, 43, 0, 0, 0, 0, 43, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0,
			37, 0, 0, 0, 0, 37, 0, 0, 0, 0};
	}

	private static final byte _json_trans_actions[] = init__json_trans_actions_0();

	private static byte[] init__json_eof_actions_0 () {
		return new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 7, 9, 9, 9, 7, 7, 7, 7, 40, 7, 7, 7, 43, 7, 7, 7, 37, 0, 0};
	}

	private static final byte _json_eof_actions[] = init__json_eof_actions_0();

	static final int json_start = 1;
	static final int json_first_final = 76;
	static final int json_error = 0;

	static final int json_en_object = 9;
	static final int json_en_array = 49;
	static final int json_en_main = 1;

	// line 227 "JsonReader.rl"

	private final Array elements = new Array(8);
	private Object root, current;

	private void set (String name, Object value) {
		if (current instanceof OrderedMap)
			((OrderedMap)current).put(name, value);
		else if (current instanceof Array)
			((Array)current).add(value);
		else
			root = value;
	}

	protected void startObject (String name) {
		OrderedMap value = new OrderedMap();
		if (current != null) set(name, value);
		elements.add(value);
		current = value;
	}

	protected void startArray (String name) {
		Array value = new Array();
		if (current != null) set(name, value);
		elements.add(value);
		current = value;
	}

	protected void pop () {
		root = elements.pop();
		current = elements.size > 0 ? elements.peek() : null;
	}

	protected void string (String name, String value) {
		set(name, value);
	}

	protected void number (String name, float value) {
		set(name, value);
	}

	protected void bool (String name, boolean value) {
		set(name, value);
	}

	private String unescape (String value) {
		int length = value.length();
		StringBuilder buffer = new StringBuilder(length + 16);
		for (int i = 0; i < length;) {
			char c = value.charAt(i++);
			if (c != '\\') {
				buffer.append(c);
				continue;
			}
			if (i == length) break;
			c = value.charAt(i++);
			if (c == 'u') {
				buffer.append(Character.toChars(Integer.parseInt(value.substring(i, i + 4), 16)));
				i += 4;
				continue;
			}
			switch (c) {
			case '"':
			case '\\':
			case '/':
				break;
			case 'b':
				c = '\b';
				break;
			case 'f':
				c = '\f';
				break;
			case 'n':
				c = '\n';
				break;
			case 'r':
				c = '\r';
				break;
			case 't':
				c = '\t';
				break;
			default:
				throw new SerializationException("Illegal escaped character: \\" + c);
			}
			buffer.append(c);
		}
		return buffer.toString();
	}
}
