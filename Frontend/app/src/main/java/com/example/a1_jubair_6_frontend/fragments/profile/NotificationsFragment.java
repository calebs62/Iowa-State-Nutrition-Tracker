package com.example.a1_jubair_6_frontend.fragments.profile;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.managers.NotificationDataManager;
import com.example.a1_jubair_6_frontend.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;


public class NotificationsFragment extends Fragment {
    private NotificationDataManager notificationManager;
    private ImageView backArrow;
    private SwitchMaterial switchSystemNotifications;
    private SwitchMaterial switchPushNotifications;
    private SwitchMaterial switchReminders;
    private boolean isSystemToggling = false;
    private MaterialCardView notificationTesterCard;
    private TextInputEditText notificationTitle;
    private TextInputEditText notificationMessage;
    private RadioGroup notificationTypeGroup;
    private MaterialButton testNotificationButton;
    private static final String CHANNEL_ID = "notification_test_channel";
    private int notificationId = 0;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManager = new NotificationDataManager(requireContext());

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

        backArrow = view.findViewById(R.id.backArrow);
        switchSystemNotifications = view.findViewById(R.id.switchSystemNotifications);
        switchPushNotifications = view.findViewById(R.id.switchPushNotifications);
        switchReminders = view.findViewById(R.id.switchReminders);

        notificationTesterCard = view.findViewById(R.id.notificationTesterCard);
        notificationTitle = view.findViewById(R.id.notificationTitle);
        notificationMessage = view.findViewById(R.id.notificationMessage);
        notificationTypeGroup = view.findViewById(R.id.notificationTypeGroup);
        testNotificationButton = view.findViewById(R.id.testNotificationButton);


        setupToolbar();
        initializeSwitches();
        loadSettings();
        setupNotificationTester();
        createNotificationChannel();

        // Get latest settings from server
        notificationManager.fetchNotificationSettings();
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
    }

    private void loadSettings() {
        boolean systemEnabled = notificationManager.getSystemNotificationsEnabled();
        boolean timeEnabled = notificationManager.getTimeNotificationsEnabled();
        boolean reminderEnabled = notificationManager.getReminderNotificationsEnabled();

        switchSystemNotifications.setOnCheckedChangeListener(null);
        switchPushNotifications.setOnCheckedChangeListener(null);
        switchReminders.setOnCheckedChangeListener(null);

        switchSystemNotifications.setChecked(systemEnabled);
        switchPushNotifications.setChecked(timeEnabled);
        switchReminders.setChecked(reminderEnabled);

        updateDependentSwitches(systemEnabled);

        initializeSwitches();
    }

    private void updateDependentSwitches(boolean systemEnabled) {
        switchPushNotifications.setEnabled(systemEnabled);
        switchReminders.setEnabled(systemEnabled);
    }

    private void showToast(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupNotificationTester() {
        // Show tester only for admin/contributor users
        if (notificationManager.getProfileDataManager().getUser().getAccounttype() == User.Account.ADMINISTRATOR ||
                notificationManager.getProfileDataManager().getUser().getAccounttype() == User.Account.CONTRIBUTOR) {
            notificationTesterCard.setVisibility(View.VISIBLE);
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