package com.intellij.application.options;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzerSettings;

import javax.swing.*;

public class ErrorHighlightingPanel {
  private JTextField myAutoreparseDelayField;
  private JCheckBox myCbShowImportPopup;
  private JTextField myMarkMinHeight;
  private JPanel myPanel;
  private JCheckBox myNextErrorGoesToErrorsFirst;
  private JCheckBox mySuppressWay;

  public void reset() {
    DaemonCodeAnalyzerSettings settings = DaemonCodeAnalyzerSettings.getInstance();
    myAutoreparseDelayField.setText(Integer.toString(settings.AUTOREPARSE_DELAY));

    myCbShowImportPopup.setSelected(settings.isImportHintEnabled());

    myMarkMinHeight.setText(Integer.toString(settings.ERROR_STRIPE_MARK_MIN_HEIGHT));
    myNextErrorGoesToErrorsFirst.setSelected(settings.NEXT_ERROR_ACTION_GOES_TO_ERRORS_FIRST);
    mySuppressWay.setSelected(settings.SUPPRESS_WARNINGS);
  }

  public void apply(){
    DaemonCodeAnalyzerSettings settings = DaemonCodeAnalyzerSettings.getInstance();

    settings.AUTOREPARSE_DELAY = getAutoReparseDelay();
    settings.setImportHintEnabled(myCbShowImportPopup.isSelected());
    settings.ERROR_STRIPE_MARK_MIN_HEIGHT = getErrorStripeMarkMinHeight();

    settings.NEXT_ERROR_ACTION_GOES_TO_ERRORS_FIRST = myNextErrorGoesToErrorsFirst.isSelected();

    settings.SUPPRESS_WARNINGS = mySuppressWay.isSelected();
  }

  public JPanel getPanel(){
    return myPanel;
  }

  private int getErrorStripeMarkMinHeight() {
    return parseInteger(myMarkMinHeight);
  }

  public boolean isModified() {
    DaemonCodeAnalyzerSettings settings = DaemonCodeAnalyzerSettings.getInstance();
    boolean isModified = settings.AUTOREPARSE_DELAY != getAutoReparseDelay();
    isModified |= myCbShowImportPopup.isSelected() != settings.isImportHintEnabled();
    isModified |= getErrorStripeMarkMinHeight() != settings.ERROR_STRIPE_MARK_MIN_HEIGHT;
    isModified |= myNextErrorGoesToErrorsFirst.isSelected() != settings.NEXT_ERROR_ACTION_GOES_TO_ERRORS_FIRST;
    isModified |= mySuppressWay.isSelected() != settings.SUPPRESS_WARNINGS;
    if (isModified) return true;
    return false;
  }


  private int getAutoReparseDelay() {
    return parseInteger(myAutoreparseDelayField);
  }

  private static int parseInteger(final JTextField textField) {
    try {
      int delay = Integer.parseInt(textField.getText());
      if (delay < 0) {
        delay = 0;
      }
      return delay;
    }
    catch (NumberFormatException e) {
      return 0;
    }
  }
}
