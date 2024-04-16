package g313.vakulenko.hw2_4_lms;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Поля
    private ImageView buttonMenu;
    private LinearLayout buttons;
    private boolean buttonsCheck = false; // Поле включения кнопок
    private ImageView buttonPalette, buttonClear, buttonEraser;
    private ArtView art;

    // Поля для домашнего задания
    private SensorManager sensorManager; // Менеджер сенсоров устройства
    private Sensor accelerometer; // Поле акселерометра

    // Словарь для хранения соответствия id элемента View и действия при нажатии
    private Map<Integer, View.OnClickListener> clickListeners = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Привязка кнопок к разметке
        buttonMenu = findViewById(R.id.buttonMenu);
        buttons = findViewById(R.id.buttons);
        buttonPalette = findViewById(R.id.buttonPalette);
        buttonClear = findViewById(R.id.buttonClear);
        buttonEraser = findViewById(R.id.buttonEraser);
        art = findViewById(R.id.art);

        // Добавление соответствий в словарь
        clickListeners.put(R.id.buttonMenu, menuClickListener);
        clickListeners.put(R.id.buttonPalette, paletteClickListener);
        clickListeners.put(R.id.buttonClear, clearClickListener);
        clickListeners.put(R.id.buttonEraser, eraserClickListener);

        // Получение доступа к сенсорам
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Инициализация сенсора
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Обработка нажатия кнопок
        for (int id : clickListeners.keySet()) {
            findViewById(id).setOnClickListener(buttonClickListener);
        }
    }

    // Создание слушателя для сенсора (акселерометра)
    private SensorEventListener sensorEventListener = new SensorEventListener() {

        // Обработчик события (вызывается всякий раз при измерении показаний сенсора)
        @SuppressLint("SetTextI18n")
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // Получаем мультиссылку на сенсоры
            Sensor multiSensor = sensorEvent.sensor;
            // Действие при получении данных с акселерометра
            if (multiSensor.getType() == Sensor.TYPE_ACCELEROMETER) { // Если изменения произошли на акселерометре, то
                float xAccelerometer = sensorEvent.values[0]; // Ускорение по оси X (поперечное направление)
                float yAccelerometer = sensorEvent.values[1]; // Ускорение по оси Y (продольное направление)
                float zAccelerometer = sensorEvent.values[2]; // Ускорение по оси Z (вертикальное направление)
                // Определим среднее значение ускорения по всем осям
                float medianAccelerometer = (xAccelerometer + yAccelerometer + zAccelerometer) / 3;
                if (medianAccelerometer > 6) { // Если телефон был в условиях ускорения, то
                    // Исполняемый код при встряхивании
                    if (buttonsCheck) {
                        buttonsCheck = false;
                        buttons.setVisibility(View.INVISIBLE);
                    } else {
                        buttonsCheck = true;
                        buttons.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        // Метод задания точности сенсора
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Регистрация сенсоров (задание слушателя)
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL); // (слушатель, сенсор - аксерометр, время обновления - среднее)
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Отзыв регистрации сенсоров (отключение слушателя)
        sensorManager.unregisterListener(sensorEventListener);
    }

    // Слушатель для кнопок
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Получаем слушателя по id кнопки и вызываем его onClick()
            View.OnClickListener listener = clickListeners.get(view.getId());
            if (listener != null) {
                listener.onClick(view);
            }
        }
    };

    // Новый слушатель для кнопки меню
    private View.OnClickListener menuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (buttonsCheck) {
                buttonsCheck = false;
                buttons.setVisibility(View.INVISIBLE);
            } else {
                buttonsCheck = true;
                buttons.setVisibility(View.VISIBLE);
            }
        }
    };

    // Новый слушатель для кнопки очистки
    private View.OnClickListener clearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Код для очистки View
            AlertDialog.Builder broomDialog = new AlertDialog.Builder(MainActivity.this); // Создание диалогового окна типа AlertDialog
            broomDialog.setTitle("Очистка рисунка"); // Заголовок диалогового окна
            broomDialog.setMessage("Очистить область рисования (имеющийся рисунок будет удалён)?"); // Сообщение диалога

            broomDialog.setPositiveButton("Да", new DialogInterface.OnClickListener(){ // Пункт выбора "да"
                public void onClick(DialogInterface dialog, int which){
                    art.clearCanvas(); // Метод очистки кастомизированного View
                    dialog.dismiss(); // Закрыть диалог
                }
            });
            broomDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener(){  // Пункт выбора "нет"
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel(); // Выход из диалога
                }
            });
            broomDialog.show(); // Отображение на экране данного диалога
        }
    };

    // Новый слушатель для кнопки палитры
    private View.OnClickListener paletteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Код для открытия палитры
            showColorPaletteDialog();
        }
    };

    // Новый слушатель для кнопки резинки
    private View.OnClickListener eraserClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Код для выбора резинки
            Toast.makeText(MainActivity.this, "Выбор резинки", Toast.LENGTH_SHORT).show();
        }
    };

    // Метод для отображения палитры выбора цвета
    private void showColorPaletteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите цвет");

        // Создание списка цветов
        final String[] colors = {"Чёрный", "Красный", "Синий", "Зелёный"};
        final int[] colorValues = {Color.BLACK, Color.RED, Color.BLUE, Color.GREEN};

        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedColor = colorValues[which];
                art.setColor(selectedColor);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
