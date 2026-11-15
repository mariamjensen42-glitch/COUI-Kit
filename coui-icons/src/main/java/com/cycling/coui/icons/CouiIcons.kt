package com.cycling.coui.icons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * COUI 的矢量与位图图标，来源见 ICON_SOURCES.md。
 * 使用 M3 Icon 保留图标原色时，需传入 Color.Unspecified。
 */
object CouiIcons {

    private object Monochrome {
        val Back: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_back_filled)

        val Next: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_next_filled)

        val Close: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_close_filled)

        val Add: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_add_filled)

        val DarkLight: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_dark_light_filled)

        val ListFilter: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_list_filter_outlined)

        val More: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_more_outlined)

        val Scan: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_scan_outlined)
    }

    object Filled {
        /** com.android.settings:drawable/coui_back_arrow_normal。 */
        val Back: ImageVector
            @Composable get() = Monochrome.Back

        /** com.android.settings:drawable/coui_btn_next_normal。 */
        val Next: ImageVector
            @Composable get() = Monochrome.Next

        /** com.android.settings:drawable/ic_close。 */
        val Close: ImageVector
            @Composable get() = Monochrome.Close

        /** com.coloros.alarmclock:drawable/color_floating_button_icon_add。 */
        val Add: ImageVector
            @Composable get() = Monochrome.Add

        /** com.android.settings:drawable/dark_theme。 */
        val DarkLight: ImageVector
            @Composable get() = Monochrome.DarkLight

        /** com.android.settings:drawable/settings_brightness_ic。 */
        val Sun: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_sun_filled)

        /** com.android.settings:drawable/settings_notify_center_ic。 */
        val Bell: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_bell_filled)

        /** com.android.settings:drawable/settings_personalise_ic。 */
        val MultiPhone: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_multi_phone_filled)

        /** com.android.settings:drawable/settings_sound_settings_ic。 */
        val Volume: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_volume_filled)

        val Plane: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_plane_filled)

        val Wifi: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_wifi_filled)

        val Bluetooth: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_bluetooth_filled)

        val Transfer: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_transfer_filled)

        val Satellite: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_satellite_filled)

        val SatelliteBD: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_satellite_bd_filled)

        val Radar: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_radar_filled)

        val Foldable: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_foldable_filled)

        val FlipPhone: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_flip_phone_filled)

        val Lock: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_lock_filled)

        val ShieldUser: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_shield_user_filled)

        val Sos: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_sos_filled)

        val Location: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_location_filled)

        val Wallet: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_wallet_filled)

        val Health: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_health_filled)

        val Grid: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_grid_filled)

        val Battery: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_battery_filled)

        val PanelEnterRight: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_panel_enter_right_filled)

        val PanelEnterLeft: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_panel_enter_left_filled)

        val Fan: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_fan_filled)

        val Rings: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_rings_filled)

        val Accessibility: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_accessibility_filled)

        val AI: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_ai_filled)

        val Gamepad: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_gamepad_filled)

        val CloudLink: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_cloud_link_filled)

        /** com.heytap.cloud:drawable/ic_homepage_tab_cloud_space_checked。 */
        val CloudData: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_cloud_data_filled)

        val SettingDetail: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_setting_detail_filled)

        val Phone: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_phone_filled)

        val UserCircle: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_user_circle_filled)

        val Google: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_google_filled)

        val Certificate: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_certificate_filled)

        val Lab: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_lab_filled)

        val BookOpen: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_book_open_filled)

        /** com.oplus.securitypermission:drawable/permission_list_ic_cameragroup_default。 */
        val Camera: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_camera_filled)

        /** com.oplus.securitypermission:drawable/permission_list_ic_microphonegroup_default。 */
        val Microphone: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_microphone_filled)

        /** com.oplus.securitypermission:drawable/permission_list_ic_readmediavisual_default。 */
        val Photo: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_photo_filled)

        /** com.oplus.securitypermission:drawable/permission_list_ic_contactsgroup_default。 */
        val Users: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_users_filled)

        /** com.oplus.securitypermission:drawable/permission_list_ic_callloggroup_default。 */
        val Call: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_call_filled)

        /** com.oplus.securitypermission:drawable/privacy_permission_manager_ic。 */
        val GridSliders: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_grid_sliders_filled)

        /** com.oplus.securitypermission:drawable/privacy_apps_lock_ic。 */
        val SquareLock: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_square_lock_filled)

        /** com.oplus.securitypermission:drawable/privacy_apps_hidden_ic。 */
        val SquareEyeClosed: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_square_eye_closed_filled)

        /** com.oplus.securitypermission:drawable/security_data_protect_kit_ic。 */
        val SafeBox: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_safe_box_filled)

        /** com.oplus.securitypermission:drawable/security_private_info_protect_ic。 */
        val PrivacyLock: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_privacy_lock_filled)

        /** com.oplus.securitypermission:drawable/privacy_phone_manager_ic。 */
        val ShieldFlash: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_shield_flash_filled)

        /** com.oplus.securitypermission:drawable/privacy_payment_protection_ic。 */
        val WalletShield: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_wallet_shield_filled)

        /** com.oplus.securitypermission:drawable/privacy_common_information_password_ic。 */
        val ShieldKey: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_shield_key_filled)

        /** com.oplus.securitypermission:drawable/multi_system_ic。 */
        val PhoneStack: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_phone_stack_filled)

        /** com.oplus.securitypermission:drawable/privacy_system_enhanced_protection_ic。 */
        val ShieldCheck: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_shield_check_filled)

        /** 当前日夜模式的应用升级图，含原始白色前景。 */
        val AppUpdate: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_app_update_filled)

        /** 生成仅更改背景色的应用升级图，并保留原始白色前景。 */
        @Composable
        fun AppUpdate(color: Color): ImageVector {
            val icon = AppUpdate
            return remember(icon, color) { icon.withAppUpdateBackground(color) }
        }

        /** com.coloros.gallery3d:drawable/main_tab_album_icon_selected。 */
        val Album: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_album_filled)

        /** com.coloros.gallery3d:drawable/main_tab_creation_icon_selected。 */
        val WandSpark: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_wand_spark_filled)

        val ListFilter: ImageVector
            @Composable get() = Monochrome.ListFilter

        val More: ImageVector
            @Composable get() = Monochrome.More

        val Scan: ImageVector
            @Composable get() = Monochrome.Scan

        /** 保留账号页设置图标的外轮廓与中心孔。 */
        val Setting: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_setting_filled)

        /** 填实云服务耳机图标的耳罩。 */
        val Headset: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_headset_filled)

        /** com.android.settings:drawable/abc_ic_menu_share_mtrl_alpha。 */
        val Share: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_share_filled)

        /** com.android.settings:drawable/abc_ic_menu_copy_mtrl_am_alpha。 */
        val Copy: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_copy_filled)

        /** com.android.settings:drawable/menu_ic_send_normal。 */
        val Send: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_send_filled)

        /** com.android.settings:drawable/ic_upload。 */
        val Upload: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_upload_filled)

        /** com.android.settings:drawable/ic_settings_print。 */
        val Print: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_print_filled)

        /** com.android.systemui:drawable/ic_oplus_media_panel_action_play。 */
        val Play: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_play_filled)

        /** com.android.systemui:drawable/ic_oplus_media_panel_action_pause。 */
        val Pause: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_pause_filled)

        /** com.android.systemui:drawable/ic_oplus_media_panel_action_pre。 */
        val Previous: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_previous_filled)

        /** com.android.systemui:drawable/ic_oplus_media_panel_action_next。 */
        val NextTrack: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_next_track_filled)

        /** com.android.systemui:drawable/oplus_media_output_icon_volume_off。 */
        val VolumeOff: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_volume_off_filled)

        /** com.android.settings:drawable/ic_volume_ringer_vibrate。 */
        val Vibrate: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_vibrate_filled)

        /** com.android.settings:drawable/ic_hotspot。 */
        val Hotspot: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_hotspot_filled)

        /** com.android.settings:drawable/ic_vpn_key。 */
        val Vpn: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_vpn_filled)

        /** com.android.settings:drawable/ic_nfc。 */
        val Nfc: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_nfc_filled)

        /** com.android.settings:drawable/ic_cast_24dp。 */
        val Cast: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_cast_filled)

        /** com.android.settings:drawable/ic_battery_charging_full。 */
        val BatteryCharging: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_battery_charging_filled)

        /** com.android.settings:drawable/ic_system_update。 */
        val SystemUpdate: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_system_update_filled)

        /** com.android.settings:drawable/ic_storage。 */
        val Storage: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_storage_filled)

        /** com.android.settings:drawable/ic_settings_data_usage。 */
        val DataUsage: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_data_usage_filled)

        /** com.android.settings:drawable/ic_settings_language。 */
        val Language: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_language_filled)

        /** com.android.settings:drawable/ic_qrcode_24dp。 */
        val QrCode: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_qr_code_filled)

        /** com.android.settings:drawable/ic_clock_black_24dp。 */
        val Clock: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_clock_filled)

        /** com.android.settings:drawable/material_ic_calendar_black_24dp。 */
        val Calendar: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_calendar_filled)

        /** com.android.settings:drawable/ic_keyboard_black_24dp。 */
        val Keyboard: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_keyboard_filled)

        /** com.android.settings:drawable/ic_apps_alt。 */
        val Apps: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_apps_filled)

        /** com.android.settings:drawable/ic_person。 */
        val Person: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_person_filled)

        /** com.android.settings:drawable/ic_chat_bubble_24dp。 */
        val Chat: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_chat_filled)

        /** com.android.settings:drawable/ic_fingerprint_24dp。 */
        val Fingerprint: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_fingerprint_filled)

        /** com.android.settings:drawable/ic_privacy_shield_24dp。 */
        val PrivacyShield: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_privacy_shield_filled)

        /** com.android.settings:drawable/ic_do_not_disturb_on_24dp。 */
        val DoNotDisturb: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_do_not_disturb_filled)

        /** com.android.systemui:drawable/ic_open_in_new。 */
        val OpenInNew: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_open_in_new_filled)

        /** com.android.systemui:drawable/vd_flashlight。 */
        val Flashlight: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_flashlight_filled)

        /** com.android.settings:drawable/ic_help。 */
        val Help: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_help_filled)

        /** com.android.systemui:drawable/ic_video。 */
        val Video: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_video_filled)

        /** com.android.systemui:drawable/ic_doc_document。 */
        val Document: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_document_filled)

        /** com.android.settings:drawable/ic_notifications_off_24dp。 */
        val NotificationsOff: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_notifications_off_filled)

        /** com.android.systemui:drawable/status_bar_qs_mic_blocked。 */
        val MicOff: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_mic_off_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_file。 */
        val ClearFile: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_clear_file_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_photo。 */
        val ClearPhoto: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_clear_photo_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_doc_file。 */
        val ClearDoc: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_clear_doc_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_recently_deleted。 */
        val RecycleBin: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_recycle_bin_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_cloud_gallery。 */
        val CloudGallery: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_cloud_gallery_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_unused_app。 */
        val ClearUnusedApp: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_clear_unused_app_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_sdcard。 */
        val SdCard: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_sd_card_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_no_network。 */
        val NoNetwork: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_no_network_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_findphone。 */
        val FindPhone: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_find_phone_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_startup_app。 */
        val StartupApp: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_startup_app_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_apk_file。 */
        val ClearApk: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_clear_apk_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_application。 */
        val ClearApp: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_clear_app_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_widget。 */
        val ClearWidget: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_clear_widget_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_add_shortcut。 */
        val AddShortcut: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_add_shortcut_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_in_app_clear。 */
        val ClearInApp: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_clear_in_app_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_file_dedup。 */
        val DedupFile: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_dedup_file_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_app_compress。 */
        val CompressApp: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_compress_app_filled)

        /** com.coloros.phonemanager:drawable/ic_download_app。 */
        val AppDownload: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_app_download_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_idle_apps。 */
        val IdleApp: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_idle_app_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_power_saving。 */
        val PowerSaving: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_power_saving_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_channel_network_acceleration。 */
        val NetAcceleration: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_net_acceleration_filled)

        /** com.coloros.phonemanager:drawable/recommend_opt_system_boost。 */
        val SystemBoost: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_system_boost_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_large_file。 */
        val LargeFile: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_large_file_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_permissions。 */
        val AppPermission: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_app_permission_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_app_control_risk。 */
        val RiskBehavior: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_risk_behavior_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_anti_harassment。 */
        val AntiHarassment: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_anti_harassment_filled)

        /** com.coloros.phonemanager:drawable/recommend_opt_virus_risk。 */
        val VirusRisk: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_virus_risk_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_duplicate_file。 */
        val DuplicateFile: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_duplicate_file_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_app_lock。 */
        val AppLock: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_app_lock_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_unencrypted_networks。 */
        val UnencryptedNetwork: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_unencrypted_network_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_audio。 */
        val ClearAudio: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_clear_audio_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_video。 */
        val ClearVideo: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_clear_video_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_update_virus_data。 */
        val VirusUpdate: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_virus_update_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_usb。 */
        val Usb: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_usb_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_smart_anti_fraud。 */
        val AntiFraud: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_anti_fraud_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_smart_connect_wlan。 */
        val SmartNetwork: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_smart_network_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_privacy_protect。 */
        val PrivacyProtect: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_privacy_protect_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_notification。 */
        val NotificationManage: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_notification_manage_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_network_abnormality。 */
        val NetworkAbnormal: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_network_abnormal_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_device_admn。 */
        val DeviceAdmin: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_device_admin_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_finger_scan。 */
        val FingerScan: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_finger_scan_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_app_management。 */
        val AppManagement: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_app_management_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_high_performance。 */
        val HighPerformance: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_high_performance_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_cloud_service。 */
        val CloudService: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_cloud_service_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_app_risk_behavior_interception。 */
        val RiskInterception: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_risk_interception_filled)

        /** com.coloros.phonemanager:drawable/main_tool_recommend_app_usage_icon。 */
        val AppUsage: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_app_usage_filled)

        /** com.coloros.phonemanager:drawable/recommend_systemopt_icon。 */
        val SystemOpt: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_system_opt_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_login。 */
        val LoginDevice: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_login_device_filled)

        /** com.android.settings:drawable/settings_face_password_icon。 */
        val FaceUnlock: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_face_unlock_filled)

        /** com.coloros.phonemanager:drawable/ic_app_one_key_opt。 */
        val OneKeyOpt: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_one_key_opt_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_other_system_resource。 */
        val SystemResource: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_system_resource_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_low_storage。 */
        val LowStorage: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_low_storage_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_app_clean。 */
        val AppClean: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_app_clean_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_dev。 */
        val DevMode: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_dev_mode_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_ota。 */
        val Ota: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_ota_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_app_over_authored。 */
        val OverAuthorized: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_over_authorized_filled)

        /** com.coloros.phonemanager:drawable/recommend_virus_icon。 */
        val Virus: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_virus_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_accessibility。 */
        val AccessibilityRisk: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_accessibility_risk_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_backlight。 */
        val Backlight: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_backlight_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_recent_lock_v2。 */
        val RecentLock: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_recent_lock_filled)

        /** com.android.settings:drawable/ic_guarantee。 */
        val Guarantee: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_guarantee_filled)

        /** com.android.settings:drawable/ic_hearing。 */
        val Hearing: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_hearing_filled)

        /** com.android.settings:drawable/ic_swap。 */
        val Swap: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_swap_filled)

        /** com.android.settings:drawable/restore_default。 */
        val RestoreDefault: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_restore_default_filled)

        /** com.android.systemui:drawable/move_top_drawable。 */
        val MoveTop: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_move_top_filled)

        /** com.android.systemui:drawable/split_screen_full_icon。 */
        val SplitScreen: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_split_screen_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_cloud_space_update。 */
        val CloudSpaceUpdate: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_cloud_space_update_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_log。 */
        val ScanLog: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_scan_log_filled)

        /** com.coloros.phonemanager:drawable/ic_clear_cloud_space_not_enough。 */
        val CloudSpaceLow: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_cloud_space_low_filled)

        /** com.coloros.phonemanager:drawable/recommend_opt_app_manager。 */
        val AppManager: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_app_manager_filled)

        /** com.coloros.phonemanager:drawable/ic_phone_call_intercept。 */
        val CallIntercept: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_call_intercept_filled)

        /** com.coloros.phonemanager:drawable/ic_recycler_file_encryption。 */
        val FileEncryption: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_file_encryption_filled)

        /** com.coloros.phonemanager:drawable/ic_recycler_message。 */
        val RecycleMessage: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_recycle_message_filled)

        /** com.coloros.phonemanager:drawable/ic_sms_intercept。 */
        val SmsIntercept: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_sms_intercept_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_widgets_clean。 */
        val WidgetClean: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_widget_clean_filled)

        /** com.coloros.phonemanager:drawable/tool_block_mms。 */
        val BlockMms: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_block_mms_filled)

        /** com.android.systemui:drawable/capsule_stat_highlight_call。 */
        val CallHighlight: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_call_highlight_filled)

        /** com.android.systemui:drawable/stat_high_light_payment_safe。 */
        val PaymentSafe: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_payment_safe_filled)

        /** com.android.settings:drawable/phone_anti_theft_alarm。 */
        val AntiTheft: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_anti_theft_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_screen_insurance。 */
        val ScreenInsurance: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_screen_insurance_filled)

        /** com.coloros.phonemanager:drawable/ic_recycler_note。 */
        val RecycleNote: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_recycle_note_filled)

        /** com.coloros.phonemanager:drawable/ic_recycler_file_manager。 */
        val RecycleFileManager: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_recycle_file_manager_filled)

        /** com.coloros.phonemanager:drawable/ic_recycler_gallery。 */
        val RecycleGallery: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_recycle_gallery_filled)

        /** com.android.systemui:drawable/magic_key_dnd_on_icon。 */
        val DndKey: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_dnd_key_filled)

        /** com.android.systemui:drawable/status_bar_qs_brightness_icon_auto。 */
        val BrightnessAuto: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_brightness_auto_filled)

        /** com.android.systemui:drawable/contextual_edu_all_apps。 */
        val GestureAllApps: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_gesture_all_apps_filled)

        /** com.android.systemui:drawable/contextual_edu_swipe_back。 */
        val GestureSwipeBack: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_gesture_swipe_back_filled)

        /** com.android.systemui:drawable/contextual_edu_swipe_up。 */
        val GestureSwipeUp: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_gesture_swipe_up_filled)

        /** com.android.settings:drawable/bluetooth_warning_icon。 */
        val BluetoothWarning: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_bluetooth_warning_filled)

        /** com.android.systemui:drawable/ic_floating_landscape。 */
        val FloatingWindow: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_floating_window_filled)

        /** com.android.settings:drawable/ic_gesture_play_button。 */
        val GesturePlay: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_gesture_play_filled)

        /** com.android.settings:drawable/settings_palm_password_ic。 */
        val PalmUnlock: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_palm_unlock_filled)

        /** com.android.settings:drawable/settings_smart_lock_icon。 */
        val SmartLock: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_smart_lock_filled)

        /** com.coloros.phonemanager:drawable/icon_app_clean_warning。 */
        val AppCleanWarning: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_app_clean_warning_filled)

        /** com.android.settings:drawable/verify_name_icon_error。 */
        val VerifyError: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_verify_error_filled)

        /** com.coloros.phonemanager:drawable/notify_tag_clear。 */
        val TagClear: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_tag_clear_filled)

        /** com.coloros.phonemanager:drawable/notify_tag_virus。 */
        val TagVirus: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_tag_virus_filled)

        /** com.android.systemui:drawable/ic_corp_badge_off。 */
        val CorpBadgeOff: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_corp_badge_off_filled)

        /** com.android.systemui:drawable/ic_emergency_star。 */
        val EmergencyStar: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_emergency_star_filled)

        /** com.android.systemui:drawable/ic_finder_active。 */
        val Finder: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_finder_filled)

        /** com.android.systemui:drawable/tile_edit_add_icon。 */
        val TileEditAdd: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_tile_edit_add_filled)

        /** com.coloros.phonemanager:drawable/main_scan_result_network_quality_scan。 */
        val NetworkQuality: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_network_quality_filled)

        /** com.coloros.phonemanager:drawable/space_release。 */
        val SpaceRelease: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_space_release_filled)

        /** com.coloros.phonemanager:drawable/vd_app_icon。 */
        val AppPlaceholder: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_app_placeholder_filled)
    }

    object Outlined {
        /** com.android.settings:drawable/coui_search_view_icon_normal。 */
        val Search: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_search_outlined)

        val Back: ImageVector
            @Composable get() = Monochrome.Back

        val Next: ImageVector
            @Composable get() = Monochrome.Next

        val Close: ImageVector
            @Composable get() = Monochrome.Close

        val Add: ImageVector
            @Composable get() = Monochrome.Add

        val DarkLight: ImageVector
            @Composable get() = Monochrome.DarkLight

        /** com.heytap.cloud:drawable/ic_homepage_tab_cloud_service_normal。 */
        val CloudLink: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_cloud_link_outlined)

        /** com.heytap.cloud:drawable/ic_homepage_tab_cloud_space_normal。 */
        val CloudData: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_cloud_data_outlined)

        /** com.heytap.cloud:drawable/ic_homepage_lucky_center。 */
        val Gift: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_gift_outlined)

        /** com.coloros.gallery3d:drawable/main_tab_photo_icon_normal。 */
        val Photo: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_photo_outlined)

        /** com.coloros.gallery3d:drawable/main_tab_album_icon_normal。 */
        val Album: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_album_outlined)

        /** com.coloros.gallery3d:drawable/main_tab_creation_icon_normal。 */
        val WandSpark: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_wand_spark_outlined)

        /** com.coloros.gallery3d:drawable/main_ic_picked。 */
        val LayoutSpliced: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_layout_spliced_outlined)

        /** com.coloros.gallery3d:drawable/main_ic_segment_album_sort。 */
        val SliderCustom: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_slider_custom_outlined)

        /** com.coloros.gallery3d:drawable/main_ic_segment_filter。 */
        val ListFilter: ImageVector
            @Composable get() = Monochrome.ListFilter

        /** com.heytap.cloud:drawable/ic_homepage_online_service。 */
        val Headset: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_headset_outlined)

        /** com.heytap.cloud:drawable/coui_toolbar_menu_icon_more_normal。 */
        val More: ImageVector
            @Composable get() = Monochrome.More

        /** com.oplus.account:drawable/ac_ic_menu_scan。 */
        val Scan: ImageVector
            @Composable get() = Monochrome.Scan

        /** com.oplus.account:drawable/menu_ic_setting_normal。 */
        val Setting: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_setting_outlined)

        /** com.android.settings:drawable/settings_wifi_ic_oplus。 */
        val Wifi: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_wifi_outlined)

        /** com.android.settings:drawable/settings_bluetooh_ic_oplus。 */
        val Bluetooth: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_bluetooth_outlined)

        /** com.android.settings:drawable/settings_notify_center_ic_oplus。 */
        val Bell: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_bell_outlined)

        /** com.android.settings:drawable/settings_brightness_ic_oplus。 */
        val Sun: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_sun_outlined)

        /** com.android.settings:drawable/settings_sound_settings_ic_oplus。 */
        val Volume: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_volume_outlined)

        /** com.android.settings:drawable/settings_airplane_mode_ic_oplus。 */
        val Plane: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_plane_outlined)

        /** com.android.settings:drawable/settings_position_exp_ic_oplus。 */
        val Location: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_location_outlined)

        /** com.android.settings:drawable/settings_privacy_exp_ic_oplus。 */
        val ShieldUser: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_shield_user_outlined)

        /** com.android.settings:drawable/ic_check_24dp。 */
        val Check: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_check_outlined)

        /** com.android.settings:drawable/ic_delete。 */
        val Delete: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_delete_outlined)

        /** com.android.settings:drawable/ic_edit。 */
        val Edit: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_edit_outlined)

        /** com.android.settings:drawable/ic_info_outline_24dp。 */
        val Info: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_info_outlined)

        /** com.android.settings:drawable/ic_warning_24dp。 */
        val Warning: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_warning_outlined)

        /** com.android.settings:drawable/ic_chevron_right_24dp。 */
        val ChevronRight: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_chevron_right_outlined)

        /** com.android.settings:drawable/ic_history。 */
        val History: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_history_outlined)

        /** com.android.settings:drawable/ic_folder_vd_theme_24。 */
        val Folder: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_folder_outlined)

        /** com.android.settings:drawable/ic_sync。 */
        val Sync: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_sync_outlined)

        /** com.android.settings:drawable/ic_link_24dp。 */
        val Link: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_link_outlined)

        /** com.android.settings:drawable/ic_download_for_offline。 */
        val Download: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_download_outlined)

        /** com.android.settings:drawable/design_ic_visibility。 */
        val Visibility: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_visibility_outlined)

        /** com.android.settings:drawable/ic_settings_gestures。 */
        val Gesture: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_gesture_outlined)

        /** com.android.systemui:drawable/ic_error_outline。 */
        val Error: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_error_outlined)

        /** com.android.systemui:drawable/ic_arrow_down_24dp。 */
        val ArrowDown: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_arrow_down_outlined)

        /** com.android.systemui:drawable/ic_baseline_expand_more_24。 */
        val ExpandMore: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_expand_more_outlined)

        /** com.android.systemui:drawable/ic_expand_less。 */
        val ExpandLess: ImageVector
            @Composable get() = couiIconResource(R.drawable.coui_expand_less_outlined)
    }

    /** 位图原件按 COUI 主题选择日夜资源。 */
    object Bitmap {
        val IdCard: Painter
            @Composable get() = couiBitmapResource(R.drawable.coui_id_card)

        val RadarSweep: Painter
            @Composable get() = couiBitmapResource(R.drawable.coui_radar_sweep)

        val Family: Painter
            @Composable get() = couiBitmapResource(R.drawable.coui_family)

        val Smile: Painter
            @Composable get() = couiBitmapResource(R.drawable.coui_smile)

        val WarrantyCard: Painter
            @Composable get() = couiBitmapResource(R.drawable.coui_warranty_card)

        val GamepadWired: Painter
            @Composable get() = couiBitmapResource(R.drawable.coui_gamepad_wired)

        val DevicePair: Painter
            @Composable get() = couiBitmapResource(R.drawable.coui_device_pair)
    }
}
