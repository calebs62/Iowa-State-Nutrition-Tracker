package com.example.a1_jubair_6_frontend.fragments.profile;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.managers.NotificationDataManager;
import com.example.a1_jubair_6_frontend.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;


public class NotificationsFragment extends Fragment {
    private static final String TAG = "NotificationsFragment";
    private NotificationDataManager notificationManager;
    private ImageView backArrow;
    private SwitchMaterial switchSystemNotifications;
    private SwitchMaterial switchPushNotifications;
    private SwitchMaterial switchReminders;
    private boolean isSystemToggling = false;
    private SwitchMaterial switchSMSNotifications;
    private SwitchMaterial switchEmailNotifications;
    private MaterialCardView notificationTesterCard;
    private TextInputEditText notificationTitle;
    private TextInputEditText notificationMessage;
    private RadioGroup notificationTypeGroup;
    private MaterialButton testNotificationButton;
    private static final String CHANNEL_ID = "notification_test_channel";
    private int notificationId = 0;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private static final int SMS_PERMISSION_REQUEST_CODE = 100;

    private static final String EMULATOR_TEST_NUMBER = "5554";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManager = new NotificationDataManager(requireContext());

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        sendTestNotification();
                    } else {
                        showToast("Notification permission denied");
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupToolbar();
        initializeSwitches();
        loadSettings();
        setupNotificationTester();
        createNotificationChannel();

        // Get latest settings from server
        notificationManager.fetchNotificationSettings();
    }

    private void initializeViews(View view) {
        backArrow = view.findViewById(R.id.backArrow);
        switchSystemNotifications = view.findViewById(R.id.switchSystemNotifications);
        switchPushNotifications = view.findViewById(R.id.switchPushNotifications);
        switchReminders = view.findViewById(R.id.switchReminders);
        switchSMSNotifications = view.findViewById(R.id.switchSMSNotifications);
        switchEmailNotifications = view.findViewById(R.id.switchEmailNotifs);
        notificationTesterCard = view.findViewById(R.id.notificationTesterCard);
        notificationTitle = view.findViewById(R.id.notificationTitle);
        notificationMessage = view.findViewById(R.id.notificationMessage);
        notificationTypeGroup = view.findViewById(R.id.notificationTypeGroup);
        testNotificationButton = view.findViewById(R.id.testNotificationButton);
    }

    private void setupToolbar() {
        backArrow.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void initializeSwitches() {
        // System notifications switch
        switchSystemNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isSystemToggling = true;
            if (!isChecked) {
                switchPushNotifications.setChecked(false);
                switchReminders.setChecked(false);
                showToast("All notifications disabled");
            } else {
                showToast("System notifications enabled");
            }
            notificationManager.toggleSystemNotifications();
            updateDependentSwitches(isChecked);
            isSystemToggling = false;
        });

        // Push notifications switch
        switchPushNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isSystemToggling) {
                if (!switchSystemNotifications.isChecked()) {
                    buttonView.setChecked(false);
                    showToast("System notifications must be enabled first");
                    return;
                }
                notificationManager.toggleTimeNotifications();
                showToast(isChecked ? "Push notifications enabled" : "Push notifications disabled");
            }
        });

        // Reminders switch
        switchReminders.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isSystemToggling) {
                if (!switchSystemNotifications.isChecked()) {
                    buttonView.setChecked(false);
                    showToast("System notifications must be enabled first");
                    return;
                }
                notificationManager.toggleReminderNotifications();
                showToast(isChecked ? "Reminders enabled" : "Reminders disabled");
            }
        });

        // SMS switch
        switchSMSNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkAndRequestSMSPermission();
            }
            notificationManager.toggleSMSNotifications();
            showToast(isChecked ? "SMS notifications enabled" : "SMS notifications disabled");
        });

        // Email switch
        switchEmailNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            notificationManager.toggleEmailNotifications();
            showToast(isChecked ? "Email notifications enabled" : "Email notifications disabled");
        });
    }

    private void loadSettings() {
        boolean systemEnabled = notificationManager.getSystemNotificationsEnabled();
        boolean timeEnabled = notificationManager.getTimeNotificationsEnabled();
        boolean reminderEnabled = notificationManager.getReminderNotificationsEnabled();
        boolean smsEnabled = notificationManager.getSMSNotificationsEnabled();
        boolean emailEnabled = notificationManager.getEmailNotificationsEnabled();

        switchSystemNotifications.setOnCheckedChangeListener(null);
        switchPushNotifications.setOnCheckedChangeListener(null);
        switchReminders.setOnCheckedChangeListener(null);
        switchSMSNotifications.setOnCheckedChangeListener(null);
        switchEmailNotifications.setOnCheckedChangeListener(null);

        switchSystemNotifications.setChecked(systemEnabled);
        switchPushNotifications.setChecked(timeEnabled);
        switchReminders.setChecked(reminderEnabled);
        switchSMSNotifications.setChecked(smsEnabled);
        switchEmailNotifications.setChecked(emailEnabled);

        updateDependentSwitches(systemEnabled);

        initializeSwitches();
    }

    private void updateDependentSwitches(boolean systemEnabled) {
        switchPushNotifications.setEnabled(systemEnabled);
        switchReminders.setEnabled(systemEnabled);
    }

    private void setupNotificationTester() {
        if (notificationManager.getProfileDataManager().getUser().getAccounttype() == User.Account.ADMINISTRATOR ||
                notificationManager.getProfileDataManager().getUser().getAccounttype() == User.Account.CONTRIBUTOR) {
            notificationTesterCard.setVisibility(View.VISIBLE);

            if (isEmulator()) {
                TextView emulatorInfo = new TextView(requireContext());
                emulatorInfo.setText(R.string.emulator_mode_sms_will_be_sent_to_this_device);
                emulatorInfo.setTextColor(ContextCompat.getColor(requireContext(),
                        com.google.android.material.R.color.design_default_color_secondary));
                emulatorInfo.setPadding(0, 16, 0, 16);

                ViewGroup radioGroupParent = (ViewGroup) notificationTypeGroup.getParent();

                int radioGroupIndex = radioGroupParent.indexOfChild(notificationTypeGroup);
                if (radioGroupIndex != -1) {
                    radioGroupParent.addView(emulatorInfo, radioGroupIndex + 1);
                }

                RadioButton smsRadio = getView().findViewById(R.id.radioSMS);
                if (smsRadio != null) {
                    smsRadio.setText(R.string.sms_notification_emulator_self_test);
                }
            }
        }

        testNotificationButton.setOnClickListener(v -> checkAndRequestNotificationPermission());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification Test Channel";
            String description = "Channel for testing notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager =
                    requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendTestNotification() {
        String title = notificationTitle.getText() != null ?
                notificationTitle.getText().toString().trim() : "Test Notification";
        String message = notificationMessage.getText() != null ?
                notificationMessage.getText().toString().trim() : "Test Message";

        if (title.isEmpty() || message.isEmpty()) {
            showToast("Please enter both title and message");
            return;
        }

        int selectedId = notificationTypeGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            showToast("Please select a notification type");
            return;
        }

        if (selectedId == R.id.radioSMS) {
            Log.d("SMSTest", "SMS Radio button selected");
            Log.d("SMSTest", "Is Emulator: " + isEmulator());

            if (isEmulator()) {
                Log.d("SMSTest", "Detected as emulator, proceeding with emulator SMS");

                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    sendEmulatorSMS(title, message);
                } else {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                            SMS_PERMISSION_REQUEST_CODE);
                }
            } else {
                Log.d("SMSTest", "Detected as real device, proceeding with real device SMS");
                sendRealDeviceSMS(title, message);
            }
        } else if (selectedId == R.id.radioEmail) {
            sendTestEmail(title, message);
        } else {
            sendSystemNotification(title, message);
        }
    }

    private void sendSystemNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
            notificationManager.notify(notificationId++, builder.build());
            showToast("Test notification sent");
        } catch (SecurityException e) {
            showToast("Failed to send notification: " + e.getMessage());
        }
    }

    private void sendEmulatorSMS(String title, String message) {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_REQUEST_CODE);
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            String fullMessage = title + "\n" + message;

            smsManager.sendTextMessage(
                    EMULATOR_TEST_NUMBER,
                    null,
                    fullMessage,
                    null,
                    null
            );

            showToast("Test SMS sent to emulator");
            Log.d("SMS_TEST", "Sent SMS to emulator:\nTitle: " + title + "\nMessage: " + message);
        } catch (Exception e) {
            showToast("Failed to send SMS: " + e.getMessage());
            Log.e("SMS_TEST", "Error sending SMS to emulator", e);
        }
    }

    private void sendRealDeviceSMS(String title, String message) {
        Log.d("SMSTest", "Entering sendRealDeviceSMS");
        Log.d("SMSTest", "Is Emulator (double-check): " + isEmulator());

        if (isEmulator()) {
            Log.d("SMSTest", "Redirecting to emulator SMS from sendRealDeviceSMS");
            sendEmulatorSMS(title, message);
            return;
        }

        String phoneNumber = notificationManager.getProfileDataManager().getPhoneNumber();

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            showToast("Please set up your phone number in personal info first");
            return;
        }

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_REQUEST_CODE);
            return;
        }

        // Check rate limiting
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        long lastSMSTime = prefs.getLong("last_test_sms_time", 0);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastSMSTime < 60000) {
            showToast("Please wait a minute between test SMS messages");
            return;
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Send Test SMS")
                .setMessage("This will send a real SMS message to " + phoneNumber +
                        ". Carrier charges may apply. Do you want to continue?")
                .setPositiveButton("Send", (dialog, which) -> {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        String fullMessage = "Test Notification\n" + title + "\n" + message;
                        ArrayList<String> parts = smsManager.divideMessage(fullMessage);

                        smsManager.sendMultipartTextMessage(
                                phoneNumber,
                                null,
                                parts,
                                null,
                                null
                        );

                        prefs.edit().putLong("last_test_sms_time", currentTime).apply();
                        Log.d("SMS_TEST", "SMS sent successfully to " + phoneNumber);
                        showToast("Test SMS sent successfully");

                    } catch (Exception e) {
                        Log.e("SMS_TEST", "Failed to send SMS", e);
                        showToast("Failed to send SMS: " + e.getMessage());
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    showToast("SMS test cancelled");
                })
                .setCancelable(false)
                .show();
    }

    private void sendTestEmail(String title, String message) {
        String email = notificationManager.getProfileDataManager().getEmail();

        if (email == null || email.isEmpty()) {
            showToast("No email address available");
            return;
        }

        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            emailIntent.putExtra(Intent.EXTRA_TEXT, message);

            if (emailIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivity(emailIntent);
                showToast("Email app opened");
            } else {
                showToast("No email app found");
            }
        } catch (Exception e) {
            showToast("Failed to launch email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {

                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                sendTestNotification();
            }
        } else {
            sendTestNotification();
        }
    }

    private void checkAndRequestSMSPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                        SMS_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void showToast(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, retry sending SMS if it was a test
                if (notificationTypeGroup.getCheckedRadioButtonId() == R.id.radioSMS) {
                    String title = notificationTitle.getText() != null ?
                            notificationTitle.getText().toString().trim() : "Test Notification";
                    String message = notificationMessage.getText() != null ?
                            notificationMessage.getText().toString().trim() : "Test Message";

                    if (isEmulator()) {
                        sendEmulatorSMS(title, message);
                    } else {
                        sendRealDeviceSMS(title, message);
                    }
                }
                showToast("SMS permission granted");
            } else {
                showToast("SMS permission denied. Cannot send test SMS.");
                switchSMSNotifications.setChecked(false);
            }
        }
    }

    private boolean isEmulator() {
        boolean fingerprint = Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown");
        boolean model = Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MODEL.toLowerCase().contains("sdk");
        boolean product = Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("generic")
                || Build.PRODUCT.contains("sdk_gphone");
        boolean brand = Build.BRAND.contains("generic")
                || Build.BRAND.contains("google");
        boolean hardware = Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu");
        boolean manufacturer = Build.MANUFACTURER.contains("Google");

        boolean isEmulator = fingerprint || model || product || brand || hardware || manufacturer;

        Log.d("EmulatorDetection", String.format(
                "Emulator Check:\n" +
                        "Fingerprint: %s (%b)\n" +
                        "Model: %s (%b)\n" +
                        "Product: %s (%b)\n" +
                        "Brand: %s (%b)\n" +
                        "Hardware: %s (%b)\n" +
                        "Manufacturer: %s (%b)\n" +
                        "Final result: %b",
                Build.FINGERPRINT, fingerprint,
                Build.MODEL, model,
                Build.PRODUCT, product,
                Build.BRAND, brand,
                Build.HARDWARE, hardware,
                Build.MANUFACTURER, manufacturer,
                isEmulator
        ));

        return isEmulator;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        notificationTesterCard = null;
        notificationTitle = null;
        notificationMessage = null;
        notificationTypeGroup = null;
        testNotificationButton = null;
    }
}