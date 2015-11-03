/* 
 * Copyright (C) 2008 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openintents.convertcsv.opencsv;

import java.io.IOException;
import java.io.Writer;

import android.content.ContentResolver;

import android.content.Context;
import android.database.Cursor;

import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;

public class ExportCsv {

	Context mContext;
	/** 获取库Phone表字段 **/
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };

	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	public ExportCsv(Context context) {
		mContext = context;
	}

	/**
	 * @param dos
	 * @throws IOException
	 */
	public void export(Writer writer) throws IOException {

		CSVWriter csvwriter = new CSVWriter(writer);
		/*csvwriter.write("ContextName");
		csvwriter.write("PhoneNumber");
		csvwriter.writeNewline();*/

		/*
		 * csvwriter.write("Note"); csvwriter.write("Encrypted");
		 * csvwriter.write("Category"); csvwriter.writeNewline();
		 */

		/*
		 * Cursor c =
		 * mContext.getContentResolver().query(NotePad.Notes.CONTENT_URI,
		 * NotepadUtils.PROJECTION_NOTES, null, null,
		 * NotePad.Notes.DEFAULT_SORT_ORDER);
		 * 
		 * if (c != null) { int COLUMN_INDEX_NOTE =
		 * c.getColumnIndexOrThrow(NotePad.Notes.NOTE); int COLUMN_INDEX_ID =
		 * c.getColumnIndexOrThrow(NotePad.Notes._ID);
		 * 
		 * while (c.moveToNext()) { String note =
		 * c.getString(COLUMN_INDEX_NOTE); long id = c.getLong(COLUMN_INDEX_ID);
		 * 
		 * String encrypted = "0"; // Not encrypted
		 * 
		 * String category = "";
		 * 
		 * csvwriter.write(note); csvwriter.write(encrypted);
		 * csvwriter.write(category); csvwriter.writeNewline(); } }
		 */

		ContentResolver resolver = mContext.getContentResolver();

		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				csvwriter.write(contactName);
				csvwriter.write(phoneNumber);
				csvwriter.writeNewline();
			}

			phoneCursor.close();
		}

		csvwriter.close();
	}

}
