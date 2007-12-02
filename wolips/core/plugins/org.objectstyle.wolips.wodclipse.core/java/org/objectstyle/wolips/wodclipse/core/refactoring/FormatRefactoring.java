package org.objectstyle.wolips.wodclipse.core.refactoring;

import java.lang.reflect.InvocationTargetException;

import jp.aonir.fuzzyxml.FuzzyXMLDocType;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.internal.RenderContext;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IDocument;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public class FormatRefactoring implements IRunnableWithProgress {
  private WodParserCache _cache;

  public FormatRefactoring(WodParserCache cache) {
    _cache = cache;
  }

  public void run(IProgressMonitor monitor) throws InvocationTargetException {
    try {
      _cache.clearCache();
      
      FuzzyXMLDocument htmlModel = _cache.getHtmlXmlDocument();
      FuzzyXMLElement documentElement = htmlModel.getDocumentElement();
      IDocument htmlDocument = _cache.getHtmlDocument();
      
      RenderContext renderContext = new RenderContext(true);
      renderContext.setShowNewlines(true);
      renderContext.setIndentSize(2);
      renderContext.setIndentTabs(false);
      renderContext.setTrim(true);
      renderContext.setLowercaseAttributes(true);
      renderContext.setLowercaseTags(true);
      renderContext.setSpacesAroundEquals(true);
      renderContext.setSpaceInEmptyTags(true);
      renderContext.setAddMissingQuotes(true);
      
      StringBuffer htmlBuffer = new StringBuffer();
      FuzzyXMLDocType docType = htmlModel.getDocumentType();
      if (docType != null) {
        docType.toXMLString(renderContext, htmlBuffer);
      }
      for (FuzzyXMLNode node : documentElement.getChildren()) {
        node.toXMLString(renderContext, htmlBuffer);
        //htmlBuffer.append("\n");
      }
      htmlDocument.set(htmlBuffer.toString().trim());
    }
    catch (Exception e) {
      throw new InvocationTargetException(e, "Failed to reformat.");
    }
  }

  public static void run(WodParserCache cache, IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException, CoreException {
    TemplateRefactoring.processHtmlAndWod(new FormatRefactoring(cache), cache, progressMonitor);
  }
}