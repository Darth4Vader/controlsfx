/**
 * Copyright (c) 2013, ControlsFX
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.controlsfx.glyphfont;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.Node;


/**
 * Defines a {@link GlyphFont} for the FontAwesome font set (see 
 * <a href="http://fortawesome.github.io/Font-Awesome/">the FontAwesome website</a>
 * for more details). Note that at present the FontAwesome font is not distributed
 * with ControlsFX, and is instead loaded from Bootstrap CDN at runtime. 
 * 
 * <p>To use FontAwesome (or indeed any glyph font) in your JavaFX application,
 * you firstly have to get access to the FontAwesome glyph font. You do this by
 * doing the following:
 * 
 * <pre>GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");</pre>
 * 
 * <p>This code works because all glyph fonts are found dynamically at runtime
 * by the {@link GlyphFontRegistry} class, so you can simply request the font
 * set you want from there.
 * 
 * <p>Once the font set has been loaded, you can simply start creating 
 * {@link Glyph} nodes and place them in your user interface. For example:
 * 
 * <pre>new Button("", fontAwesome.fontColor(Color.RED).create(&#92;uf013));</pre>
 * 
 * <p>Of course, this requires you to know that <code>&#92;uf013</code> maps to 
 * a 'gear' icon, which is not always intuitive (especially when you re-read the 
 * code in the future). A simpler approach is to do the following:
 * 
 * <pre>new Button("", FontAwesome.Glyph.GEAR.create());</pre>
 * 
 * It is possible to achieve the same result without creating a reference to icon font by simply using 
 * {@link GlyphFontRegistry} methods
 * 
 * <pre>new Button("", GlyphFontRegistry.glyph("FontAwesome|GEAR");</pre>
 * 
 * @see GlyphFont
 * @see GlyphFontRegistry
 * @see Glyph
 */
public class FontAwesome extends GlyphFont {

	private static String fontName = "FontAwesome";
	
	private Map<String, Character> glyphs;
	
	/**
	 * The individual glyphs offered by the FontAwesome font.
	 */
	public static enum Glyph {

		GLASS('\uf000'),
		MUSIC('\uf001'),
		SEARCH('\uf002'),
		ENVELOPE_ALT('\uf003'),
		HEART('\uf004'),
		STAR('\uf005'),
		STAR_EMPTY('\uf006'),
		USER('\uf007'),
		FILM('\uf008'),
		TH_LARGE('\uf009'),
		TH('\uf00A'),
		TH_LIST('\uf00B'),
		OK('\uf00C'),
		REMOVE('\uf00D'),
		ZOOM_IN('\uf00E'),
		ZOOM_OUT('\uf010'),
		POWER_OFF('\uf011'),
		OFF('\uf011'),
		SIGNAL('\uf012'),
		GEAR('\uf013'),
		COG('\uf013'),
		TRASH('\uf014'),
		HOME('\uf015'),
		FILE_ALT('\uf016'),
		TIME('\uf017'),
		ROAD('\uf018'),
		DOWNLOAD_ALT('\uf019'),
		DOWNLOAD('\uf01A'),
		UPLOAD('\uf01B'),
		INBOX('\uf01C'),
		PLAY_CIRCLE('\uf01D'),
		ROTATE_RIGHT('\uf01E'),
		REPEAT('\uf01E'),
		REFRESH('\uf021'),
		LIST_ALT('\uf022'),
		LOCK('\uf023'),
		FLAG('\uf024'),
		HEADPHONES('\uf025'),
		VOLUME_OFF('\uf026'),
		VOLUME_DOWN('\uf027'),
		VOLUME_UP('\uf028'),
		QRCODE('\uf029'),
		BARCODE('\uf02A'),
		TAG('\uf02B'),
		TAGS('\uf02C'),
		BOOK('\uf02D'),
		BOOKMARK('\uf02E'),
		PRINT('\uf02F'),
		CAMERA('\uf030'),
		FONT('\uf031'),
		BOLD('\uf032'),
		ITALIC('\uf033'),
		TEXT_HEIGHT('\uf034'),
		TEXT_WIDTH('\uf035'),
		ALIGN_LEFT('\uf036'),
		ALIGN_CENTER('\uf037'),
		ALIGN_RIGHT('\uf038'),
		ALIGN_JUSTIFY('\uf039'),
		LIST('\uf03A'),
		INDENT_LEFT('\uf03B'),
		INDENT_RIGHT('\uf03C'),
		FACETIME_VIDEO('\uf03D'),
		PICTURE('\uf03E'),
		PENCIL('\uf040'),
		MAP_MARKER('\uf041'),
		ADJUST('\uf042'),
		TINT('\uf043'),
		EDIT('\uf044'),
		SHARE('\uf045'),
		CHECK('\uf046'),
		MOVE('\uf047'),
		STEP_BACKWARD('\uf048'),
		FAST_BACKWARD('\uf049'),
		BACKWARD('\uf04A'),
		PLAY('\uf04B'),
		PAUSE('\uf04C'),
		STOP('\uf04D'),
		FORWARD('\uf04E'),
		FAST_FORWARD('\uf050'),
		STEP_FORWARD('\uf051'),
		EJECT('\uf052'),
		CHEVRON_LEFT('\uf053'),
		CHEVRON_RIGHT('\uf054'),
		PLUS_SIGN('\uf055'),
		MINUS_SIGN('\uf056'),
		REMOVE_SIGN('\uf057'),
		OK_SIGN('\uf058'),
		QUESTION_SIGN('\uf059'),
		INFO_SIGN('\uf05A'),
		SCREENSHOT('\uf05B'),
		REMOVE_CIRCLE('\uf05C'),
		OK_CIRCLE('\uf05D'),
		BAN_CIRCLE('\uf05E'),
		ARROW_LEFT('\uf060'),
		ARROW_RIGHT('\uf061'),
		ARROW_UP('\uf062'),
		ARROW_DOWN('\uf063'),
		MAIL_FORWARD('\uf064'),
		SHARE_ALT('\uf064'),
		RESIZE_FULL('\uf065'),
		RESIZE_SMALL('\uf066'),
		PLUS('\uf067'),
		MINUS('\uf068'),
		ASTERISK('\uf069'),
		EXCLAMATION_SIGN('\uf06A'),
		GIFT('\uf06B'),
		LEAF('\uf06C'),
		FIRE('\uf06D'),
		EYE_OPEN('\uf06E'),
		EYE_CLOSE('\uf070'),
		WARNING_SIGN('\uf071'),
		PLANE('\uf072'),
		CALENDAR('\uf073'),
		RANDOM('\uf074'),
		COMMENT('\uf075'),
		MAGNET('\uf076'),
		CHEVRON_UP('\uf077'),
		CHEVRON_DOWN('\uf078'),
		RETWEET('\uf079'),
		SHOPPING_CART('\uf07A'),
		FOLDER_CLOSE('\uf07B'),
		FOLDER_OPEN('\uf07C'),
		RESIZE_VERTICAL('\uf07D'),
		RESIZE_HORIZONTAL('\uf07E'),
		BAR_CHART('\uf080'),
		TWITTER_SIGN('\uf081'),
		FACEBOOK_SIGN('\uf082'),
		CAMERA_RETRO('\uf083'),
		KEY('\uf084'),
		GEARS('\uf085'),
		COGS('\uf085'),
		COMMENTS('\uf086'),
		THUMBS_UP_ALT('\uf087'),
		THUMBS_DOWN_ALT('\uf088'),
		STAR_HALF('\uf089'),
		HEART_EMPTY('\uf08A'),
		SIGNOUT('\uf08B'),
		LINKEDIN_SIGN('\uf08C'),
		PUSHPIN('\uf08D'),
		EXTERNAL_LINK('\uf08E'),
		SIGNIN('\uf090'),
		TROPHY('\uf091'),
		GITHUB_SIGN('\uf092'),
		UPLOAD_ALT('\uf093'),
		LEMON('\uf094'),
		PHONE('\uf095'),
		UNCHECKED('\uf096'),
		CHECK_EMPTY('\uf096'),
		BOOKMARK_EMPTY('\uf097'),
		PHONE_SIGN('\uf098'),
		TWITTER('\uf099'),
		FACEBOOK('\uf09A'),
		GITHUB('\uf09B'),
		UNLOCK('\uf09C'),
		CREDIT_CARD('\uf09D'),
		RSS('\uf09E'),
		HDD('\uf0A0'),
		BULLHORN('\uf0A1'),
		BELL('\uf0A2'),
		CERTIFICATE('\uf0A3'),
		HAND_RIGHT('\uf0A4'),
		HAND_LEFT('\uf0A5'),
		HAND_UP('\uf0A6'),
		HAND_DOWN('\uf0A7'),
		CIRCLE_ARROW_LEFT('\uf0A8'),
		CIRCLE_ARROW_RIGHT('\uf0A9'),
		CIRCLE_ARROW_UP('\uf0AA'),
		CIRCLE_ARROW_DOWN('\uf0AB'),
		GLOBE('\uf0AC'),
		WRENCH('\uf0AD'),
		TASKS('\uf0AE'),
		FILTER('\uf0B0'),
		BRIEFCASE('\uf0B1'),
		FULLSCREEN('\uf0B2'),
		GROUP('\uf0C0'),
		LINK('\uf0C1'),
		CLOUD('\uf0C2'),
		BEAKER('\uf0C3'),
		CUT('\uf0C4'),
		COPY('\uf0C5'),
		PAPERCLIP('\uf0C6'),
		PAPER_CLIP('\uf0C6'),
		SAVE('\uf0C7'),
		SIGN_BLANK('\uf0C8'),
		REORDER('\uf0C9'),
		LIST_UL('\uf0CA'),
		LIST_OL('\uf0CB'),
		STRIKETHROUGH('\uf0CC'),
		UNDERLINE('\uf0CD'),
		TABLE('\uf0CE'),
		MAGIC('\uf0D0'),
		TRUCK('\uf0D1'),
		PINTEREST('\uf0D2'),
		PINTEREST_SIGN('\uf0D3'),
		GOOGLE_PLUS_SIGN('\uf0D4'),
		GOOGLE_PLUS('\uf0D5'),
		MONEY('\uf0D6'),
		CARET_DOWN('\uf0D7'),
		CARET_UP('\uf0D8'),
		CARET_LEFT('\uf0D9'),
		CARET_RIGHT('\uf0DA'),
		COLUMNS('\uf0DB'),
		SORT('\uf0DC'),
		SORT_DOWN('\uf0DD'),
		SORT_UP('\uf0DE'),
		ENVELOPE('\uf0E0'),
		LINKEDIN('\uf0E1'),
		ROTATE_LEFT('\uf0E2'),
		UNDO('\uf0E2'),
		LEGAL('\uf0E3'),
		DASHBOARD('\uf0E4'),
		COMMENT_ALT('\uf0E5'),
		COMMENTS_ALT('\uf0E6'),
		BOLT('\uf0E7'),
		SITEMAP('\uf0E8'),
		UMBRELLA('\uf0E9'),
		PASTE('\uf0EA'),
		LIGHTBULB('\uf0EB'),
		EXCHANGE('\uf0EC'),
		CLOUD_DOWNLOAD('\uf0ED'),
		CLOUD_UPLOAD('\uf0EE'),
		USER_MD('\uf0F0'),
		STETHOSCOPE('\uf0F1'),
		SUITCASE('\uf0F2'),
		BELL_ALT('\uf0F3'),
		COFFEE('\uf0F4'),
		FOOD('\uf0F5'),
		FILE_TEXT_ALT('\uf0F6'),
		BUILDING('\uf0F7'),
		HOSPITAL('\uf0F8'),
		AMBULANCE('\uf0F9'),
		MEDKIT('\uf0FA'),
		FIGHTER_JET('\uf0FB'),
		BEER('\uf0FC'),
		H_SIGN('\uf0FD'),
		PLUS_SIGN_ALT('\uf0FE'),
		DOUBLE_ANGLE_LEFT('\uf100'),
		DOUBLE_ANGLE_RIGHT('\uf101'),
		DOUBLE_ANGLE_UP('\uf102'),
		DOUBLE_ANGLE_DOWN('\uf103'),
		ANGLE_LEFT('\uf104'),
		ANGLE_RIGHT('\uf105'),
		ANGLE_UP('\uf106'),
		ANGLE_DOWN('\uf107'),
		DESKTOP('\uf108'),
		LAPTOP('\uf109'),
		TABLET('\uf10A'),
		MOBILE_PHONE('\uf10B'),
		CIRCLE_BLANK('\uf10C'),
		QUOTE_LEFT('\uf10D'),
		QUOTE_RIGHT('\uf10E'),
		SPINNER('\uf110'),
		CIRCLE('\uf111'),
		MAIL_REPLY('\uf112'),
		REPLY('\uf112'),
		GITHUB_ALT('\uf113'),
		FOLDER_CLOSE_ALT('\uf114'),
		FOLDER_OPEN_ALT('\uf115'),
		EXPAND_ALT('\uf116'),
		COLLAPSE_ALT('\uf117'),
		SMILE('\uf118'),
		FROWN('\uf119'),
		MEH('\uf11A'),
		GAMEPAD('\uf11B'),
		KEYBOARD('\uf11C'),
		FLAG_ALT('\uf11D'),
		FLAG_CHECKERED('\uf11E'),
		TERMINAL('\uf120'),
		CODE('\uf121'),
		REPLY_ALL('\uf122'),
		MAIL_REPLY_ALL('\uf122'),
		STAR_HALF_FULL('\uf123'),
		STAR_HALF_EMPTY('\uf123'),
		LOCATION_ARROW('\uf124'),
		CROP('\uf125'),
		CODE_FORK('\uf126'),
		UNLINK('\uf127'),
		QUESTION('\uf128'),
		INFO('\uf129'),
		EXCLAMATION('\uf12A'),
		SUPERSCRIPT('\uf12B'),
		SUBSCRIPT('\uf12C'),
		ERASER('\uf12D'),
		PUZZLE_PIECE('\uf12E'),
		MICROPHONE('\uf130'),
		MICROPHONE_OFF('\uf131'),
		SHIELD('\uf132'),
		CALENDAR_EMPTY('\uf133'),
		FIRE_EXTINGUISHER('\uf134'),
		ROCKET('\uf135'),
		MAXCDN('\uf136'),
		CHEVRON_SIGN_LEFT('\uf137'),
		CHEVRON_SIGN_RIGHT('\uf138'),
		CHEVRON_SIGN_UP('\uf139'),
		CHEVRON_SIGN_DOWN('\uf13A'),
		HTML5('\uf13B'),
		CSS3('\uf13C'),
		ANCHOR('\uf13D'),
		UNLOCK_ALT('\uf13E'),
		BULLSEYE('\uf140'),
		ELLIPSIS_HORIZONTAL('\uf141'),
		ELLIPSIS_VERTICAL('\uf142'),
		RSS_SIGN('\uf143'),
		PLAY_SIGN('\uf144'),
		TICKET('\uf145'),
		MINUS_SIGN_ALT('\uf146'),
		CHECK_MINUS('\uf147'),
		LEVEL_UP('\uf148'),
		LEVEL_DOWN('\uf149'),
		CHECK_SIGN('\uf14A'),
		EDIT_SIGN('\uf14B'),
		EXTERNAL_LINK_SIGN('\uf14C'),
		SHARE_SIGN('\uf14D'),
		COMPASS('\uf14E'),
		COLLAPSE('\uf150'),
		COLLAPSE_TOP('\uf151'),
		EXPAND('\uf152'),
		EURO('\uf153'),
		EUR('\uf153'),
		GBP('\uf154'),
		DOLLAR('\uf155'),
		USD('\uf155'),
		RUPEE('\uf156'),
		INR('\uf156'),
		YEN('\uf157'),
		JPY('\uf157'),
		RENMINBI('\uf158'),
		CNY('\uf158'),
		WON('\uf159'),
		KRW('\uf159'),
		BITCOIN('\uf15A'),
		BTC('\uf15A'),
		FILE('\uf15B'),
		FILE_TEXT('\uf15C'),
		SORT_BY_ALPHABET('\uf15D'),
		SORT_BY_ALPHABET_ALT('\uf15E'),
		SORT_BY_ATTRIBUTES('\uf160'),
		SORT_BY_ATTRIBUTES_ALT('\uf161'),
		SORT_BY_ORDER('\uf162'),
		SORT_BY_ORDER_ALT('\uf163'),
		THUMBS_UP('\uf164'),
		THUMBS_DOWN('\uf165'),
		YOUTUBE_SIGN('\uf166'),
		YOUTUBE('\uf167'),
		XING('\uf168'),
		XING_SIGN('\uf169'),
		YOUTUBE_PLAY('\uf16A'),
		DROPBOX('\uf16B'),
		STACKEXCHANGE('\uf16C'),
		INSTAGRAM('\uf16D'),
		FLICKR('\uf16E'),
		ADN('\uf170'),
		BITBUCKET('\uf171'),
		BITBUCKET_SIGN('\uf172'),
		TUMBLR('\uf173'),
		TUMBLR_SIGN('\uf174'),
		LONG_ARROW_DOWN('\uf175'),
		LONG_ARROW_UP('\uf176'),
		LONG_ARROW_LEFT('\uf177'),
		LONG_ARROW_RIGHT('\uf178'),
		APPLE('\uf179'),
		WINDOWS('\uf17A'),
		ANDROID('\uf17B'),
		LINUX('\uf17C'),
		DRIBBBLE('\uf17D'),
		SKYPE('\uf17E'),
		FOURSQUARE('\uf180'),
		TRELLO('\uf181'),
		FEMALE('\uf182'),
		MALE('\uf183'),
		GITTIP('\uf184'),
		SUN('\uf185'),
		MOON('\uf186'),
		ARCHIVE('\uf187'),
		BUG('\uf188'),
		VK('\uf189'),
		WEIBO('\uf18A'),
		RENREN('\uf18B');
		
		private final Character ch;
		
		Glyph( Character ch ) {
			this.ch = ch;
		}
		
		public Character getChar() {
			return ch;
		}
		
		public Node create() {
		    return GlyphFontRegistry.glyph(fontName, name());
		}
	};
	
	/**
	 * Do not call this constructor directly - instead access the 
	 * {@link FontAwesome.Glyph} public static enumeration (and call the 
	 * {@link FontAwesome.Glyph#create()} method to create the glyph nodes), or
	 * use the {@link GlyphFontRegistry} class to get access.
	 */
	public FontAwesome() {
		
		super(fontName, 14, "http://netdna.bootstrapcdn.com/font-awesome/3.2.1/font/fontawesome-webfont.ttf" ); 
		
		Map<String, Character> map = new HashMap<>();
		for (Glyph e:  Glyph.values()) {
			map.put(e.name(), e.getChar());
		}
		glyphs = Collections.unmodifiableMap(map);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override public Map<String, Character> getGlyphs() {
		return glyphs;
	}
}
