package com.hideakin.yanimu.xml;

import java.util.List;

public class DocumentTypeDeclaration extends Token {

	public final Token[] layout;
	public final String name;

	public DocumentTypeDeclaration(List<Token> tokenList, String name) {
		super(DOCTYPE_DECL, tokenList);
		this.layout = tokenList.toArray(new Token[tokenList.size()]);
		this.name = name;
	}

}
