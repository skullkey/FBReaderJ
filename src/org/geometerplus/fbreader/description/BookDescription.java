/*
 * Copyright (C) 2007-2008 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */
package org.geometerplus.fbreader.description;

import java.util.*;
import org.geometerplus.zlibrary.core.util.*;

import org.geometerplus.fbreader.description.Author.MultiAuthor;
import org.geometerplus.fbreader.description.Author.SingleAuthor;
import org.geometerplus.fbreader.formats.FormatPlugin;
import org.geometerplus.fbreader.formats.FormatPlugin.PluginCollection;
import org.geometerplus.fbreader.option.FBOptions;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.options.ZLBooleanOption;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.core.options.ZLStringOption;

public class BookDescription {
	private Author myAuthor;
	private	String myTitle = "";
	private	String mySequenceName = "";
	private	int myNumberInSequence = 0;
	private	final String myFileName;
	private	String myLanguage = "";
	private	String myEncoding = "";
	private final static HashMap ourDescriptions = new HashMap();
	
	private static final String EMPTY = "";
	private static final String UNKNOWN = "unknown";
	
	public static BookDescription getDescription(String fileName) {
		return getDescription(fileName, true); 
	} 
	
	public static BookDescription getDescription(String fileName, boolean checkFile) {
		if (fileName == null) {
			return null;
		}
		String physicalFileName = new ZLFile(fileName).getPhysicalFilePath();
		ZLFile file = new ZLFile(physicalFileName);
		if (checkFile && !file.exists()) {
			return null;
		}
		BookDescription description = (BookDescription)ourDescriptions.get(fileName);
		if (description == null) {
			description = new BookDescription(fileName);
			ourDescriptions.put(fileName, description);
		}
		if (!checkFile || BookDescriptionUtil.checkInfo(file)) {
			BookInfo info = new BookInfo(fileName);
			description.myAuthor = SingleAuthor.create(info.AuthorDisplayNameOption.getValue(), info.AuthorSortKeyOption.getValue());
			description.myTitle = info.TitleOption.getValue();
			description.mySequenceName = info.SequenceNameOption.getValue();
			description.myNumberInSequence = info.NumberInSequenceOption.getValue();
			description.myLanguage = info.LanguageOption.getValue();
			description.myEncoding = info.EncodingOption.getValue();
			if (info.isFull()) {
				return description;
			}
		} else {
			if (physicalFileName != fileName) {
				BookDescriptionUtil.resetZipInfo(file);
			}
			BookDescriptionUtil.saveInfo(file);
		}
		ZLFile bookFile = new ZLFile(fileName);
		
		FormatPlugin plugin = PluginCollection.instance().getPlugin(bookFile, false);
		if ((plugin == null) || !plugin.readDescription(fileName, description)) {
			return null;
		}

		if (description.myTitle.length() == 0) {
			description.myTitle = bookFile.getName(true);
		}
		Author author = description.myAuthor;
		if (author == null || author.getDisplayName().length() == 0) {
			description.myAuthor = SingleAuthor.create();
		}
		if (description.myEncoding.length() == 0) {
			description.myEncoding = "auto";
		}
		{
			BookInfo info = new BookInfo(fileName);
			info.AuthorDisplayNameOption.setValue(description.myAuthor.getDisplayName());
			info.AuthorSortKeyOption.setValue(description.myAuthor.getSortKey());
			info.TitleOption.setValue(description.myTitle);
			info.SequenceNameOption.setValue(description.mySequenceName);
			info.NumberInSequenceOption.setValue(description.myNumberInSequence);
			info.LanguageOption.setValue(description.myLanguage);
			info.EncodingOption.setValue(description.myEncoding);
			info.IsSequenceDefinedOption.setValue(true);
		}
		return description;
	}


	private BookDescription(String fileName) {
		myFileName = fileName;
		myAuthor = null;
		myNumberInSequence = 0;
	}

	public Author getAuthor() {
		return myAuthor;
	}
	
	public String getTitle() {
		return myTitle;
	}
	
	public String getSequenceName() {
		return mySequenceName;
	}
	
	public int getNumberInSequence() {
		return myNumberInSequence; 
	}
	
	public String getFileName() {
		return myFileName; 
	}
	
	public String getLanguage() {
		return myLanguage;
	}
	
	public String getEncoding() {
		return myEncoding;
	}
	
	
	
	public static class BookInfo {
		// This option is used to fix problem with missing sequence-related options
		// in config in versions < 0.7.4k
		// It makes no sense if old fbreader was never used on your device.
		private final ZLBooleanOption IsSequenceDefinedOption;

		public BookInfo(String fileName) {
			AuthorDisplayNameOption = new ZLStringOption(FBOptions.BOOKS_CATEGORY, fileName, "AuthorDisplayName", EMPTY);
			AuthorSortKeyOption = new ZLStringOption(FBOptions.BOOKS_CATEGORY, fileName, "AuthorSortKey", EMPTY);
			TitleOption = new ZLStringOption(FBOptions.BOOKS_CATEGORY, fileName, "Title", EMPTY);
			SequenceNameOption = new ZLStringOption(FBOptions.BOOKS_CATEGORY, fileName, "Sequence", EMPTY);
			NumberInSequenceOption = new ZLIntegerRangeOption(FBOptions.BOOKS_CATEGORY, fileName, "Number in seq", 0, 100, 0);
			LanguageOption = new ZLStringOption(FBOptions.BOOKS_CATEGORY, fileName, "Language", UNKNOWN);
			EncodingOption = new ZLStringOption(FBOptions.BOOKS_CATEGORY, fileName, "Encoding", EMPTY);
			IsSequenceDefinedOption = new ZLBooleanOption(FBOptions.BOOKS_CATEGORY, fileName, "SequenceDefined", new ZLFile(fileName).getExtension().equals("fb2")); 
			
		}
	
		public boolean isFull() {
			return
			((AuthorDisplayNameOption.getValue().length() != 0) &&
			(AuthorSortKeyOption.getValue().length() != 0) &&
			(TitleOption.getValue().length() != 0) &&
			(EncodingOption.getValue().length() != 0) &&
			IsSequenceDefinedOption.getValue());
		}
		
		void reset() {
			AuthorDisplayNameOption.setValue(EMPTY);
			AuthorSortKeyOption.setValue(EMPTY);
			TitleOption.setValue(EMPTY);
			SequenceNameOption.setValue(EMPTY);
			NumberInSequenceOption.setValue(0);
			LanguageOption.setValue(UNKNOWN);
			EncodingOption.setValue(EMPTY);
		}

		private final ZLStringOption AuthorDisplayNameOption;
		private final ZLStringOption AuthorSortKeyOption;
		private final ZLStringOption TitleOption;
		private final ZLStringOption SequenceNameOption;
		private final ZLIntegerRangeOption NumberInSequenceOption;
		public final ZLStringOption LanguageOption;
		public final ZLStringOption EncodingOption;

		public ZLStringOption getAuthorSortKeyOption() {
			return AuthorSortKeyOption;
		}

		public ZLStringOption getAuthorDisplayNameOption() {
			return AuthorDisplayNameOption;
		}

		public ZLStringOption getSequenceNameOption() {
			return SequenceNameOption;
		}

		public ZLStringOption getTitleOption() {
			return TitleOption;
		}

		public ZLIntegerRangeOption getNumberInSequenceOption() {
			return NumberInSequenceOption;
		}

	}
	
	static public class WritableBookDescription  {
		private final BookDescription myDescription;
		
		public WritableBookDescription(BookDescription description) {
			myDescription = description;
		}
		
		public void addAuthor(String name) {
			addAuthor(name, "");
		}
		
		public void addAuthor(String name, String sortKey) {
			String strippedName = name;
			strippedName.trim();
			if (strippedName.length() == 0) {
				return;
			}

			String strippedKey = sortKey;
			strippedKey.trim();
			if (strippedKey.length() == 0) {
				int index = strippedName.indexOf(' ');
				if (index == -1) {
					strippedKey = strippedName;
				} else {
					strippedKey = strippedName.substring(index + 1);
					while ((index >= 0) && (strippedName.charAt(index) == ' ')) {
						--index;
					}
					strippedName = strippedName.substring(0, index + 1) + ' ' + strippedKey;
				}
			}
			Author author = SingleAuthor.create(strippedName, strippedKey);
			
			if (myDescription.myAuthor == null) {
				myDescription.myAuthor = author;
			} else {
				if (myDescription.myAuthor.isSingle()) {
					myDescription.myAuthor = MultiAuthor.create(myDescription.myAuthor);
				}
				((MultiAuthor)myDescription.myAuthor).addAuthor(author);
			}
		}
		
		public void clearAuthor() {
			myDescription.myAuthor = null;
		}
		
		public Author getAuthor() {
			return myDescription.getAuthor();
		}
		
		public String getTitle() {
			return myDescription.myTitle;
		}
		
		public void setTitle(String title) {
			myDescription.myTitle = title;
		}
		
		public String getSequenceName() {
			return myDescription.mySequenceName;
		}
		
		public void setSequenceName(String sequenceName) {
			myDescription.mySequenceName = sequenceName;
		}
		
		public int getNumberInSequence() {
			return myDescription.myNumberInSequence;
		}
		
		public void setNumberInSequence(int numberInSequence) {
			myDescription.myNumberInSequence = numberInSequence;
		}
		
		public String getFileName() {
			return myDescription.myFileName; 
		}
		
		public String getLanguage() {
			return myDescription.myLanguage;
		}
		
		public void setLanguage(String language) {
			this.myDescription.myLanguage = language;
		}
		
		public String getEncoding() {
			return myDescription.myEncoding;
		}
		
		public void setEncoding(String encoding) {
			this.myDescription.myEncoding = encoding;
		}
	};
}