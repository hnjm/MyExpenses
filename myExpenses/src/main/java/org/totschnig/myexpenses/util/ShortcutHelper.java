package org.totschnig.myexpenses.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import com.annimon.stream.Stream;

import org.totschnig.myexpenses.MyApplication;
import org.totschnig.myexpenses.R;
import org.totschnig.myexpenses.activity.ContribInfoDialogActivity;
import org.totschnig.myexpenses.activity.ExpenseEdit;
import org.totschnig.myexpenses.activity.MyExpenses;
import org.totschnig.myexpenses.activity.SimpleToastActivity;
import org.totschnig.myexpenses.model.ContribFeature;
import org.totschnig.myexpenses.widget.AbstractWidget;

import java.util.Collections;

public class ShortcutHelper {
  public static Intent createIntentForNewSplit(Context context) {
    return createIntentForNewTransaction(context, MyExpenses.TYPE_SPLIT);
  }

  public static Intent createIntentForNewTransfer(Context context) {
    return createIntentForNewTransaction(context, MyExpenses.TYPE_TRANSFER);
  }

  public static Intent createIntentForNewTransaction(Context context, int operationType) {
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_MAIN);
    intent.setComponent(new ComponentName(context.getPackageName(),
        ExpenseEdit.class.getName()));
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

    Bundle extras = new Bundle();
    extras.putBoolean(AbstractWidget.EXTRA_START_FROM_WIDGET, true);
    extras.putBoolean(AbstractWidget.EXTRA_START_FROM_WIDGET_DATA_ENTRY, true);
    extras.putInt(MyApplication.KEY_OPERATION_TYPE, operationType);
    intent.putExtras(extras);
    return intent;
  }

  @RequiresApi(api = Build.VERSION_CODES.N_MR1)
  public static void configureSplitShortcut(Context context, boolean contribEnabled) {
    ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

    Intent intent;
    if (contribEnabled) {
      intent = createIntentForNewSplit(context);
    } else {
      intent = ContribInfoDialogActivity.getIntentFor(context, ContribFeature.SPLIT_TRANSACTION);
    }
    ShortcutInfo shortcut = new ShortcutInfo.Builder(context, "split")
        .setShortLabel(context.getString(R.string.menu_create_split))
        .setIcon(Icon.createWithResource(context, R.drawable.ic_menu_split_shortcut))
        .setIntent(intent)
        .build();
    shortcutManager.addDynamicShortcuts(Collections.singletonList(shortcut));
  }

  @RequiresApi(api = Build.VERSION_CODES.N_MR1)
  public static void configureTransferShortcut(Context context, boolean transferEnabled) {
    ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

    Intent intent;
    if (transferEnabled) {
      intent = createIntentForNewTransfer(context);
    } else {
      intent = new Intent(context, SimpleToastActivity.class)
          .setAction(Intent.ACTION_MAIN)
          .putExtra(SimpleToastActivity.KEY_MESSAGE_ID, R.string.dialog_command_disabled_insert_transfer);
    }
    ShortcutInfo shortcut = new ShortcutInfo.Builder(context, "transfer")
        .setShortLabel(context.getString(R.string.menu_create_transfer))
        .setIcon(Icon.createWithResource(context, R.drawable.ic_menu_forward_shortcut))
        .setIntent(intent)
        .build();
    shortcutManager.addDynamicShortcuts(Collections.singletonList(shortcut));

  }

  @RequiresApi(api = Build.VERSION_CODES.N_MR1)
  public static void initTransferShortcut(Context context) {
    if (Stream.of(context.getSystemService(ShortcutManager.class).getDynamicShortcuts())
        .noneMatch(shortcutInfo -> shortcutInfo.getId().equals("transfer"))) {
      configureTransferShortcut(context, false);
    }
  }
}