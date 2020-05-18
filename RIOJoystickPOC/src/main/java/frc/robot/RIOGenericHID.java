package frc.robot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dgis.input.evdev.EventDevice;
import com.dgis.input.evdev.InputEvent;
import com.dgis.input.evdev.InputListener;

import edu.wpi.first.wpilibj.GenericHID;

public abstract class RIOGenericHID extends GenericHID implements InputListener {

  /*
   * Event Device for the USB Joystick/gamepad device created in the constructor
   */
  private EventDevice m_device;

  /*
   * Holds the event codes for each joystick button, in order. That is, if event
   * code 288 is button one, it is the first entry here.
   */
  private ArrayList<Integer> m_buttonEventCodes = new ArrayList<Integer>();

  /*
   * Holds the event codes for each joystick axis, in order. That is, if event
   * code 0 is axis one, it is the first entry here.
   */
  private ArrayList<Integer> m_axisEventCodes = new ArrayList<Integer>();

  /* Count of axes and buttons on this input device */
  int m_axesCount;
  int m_buttonsCount;

  /*
   * Record the state of buttons and axes as they change so we can return values
   * whenever the robot code requests one
   */
  private int m_axisStates[];
  private boolean m_buttonStates[];

  /*
   * Record any observed button presses or releases, cleared when the robot code
   * reads them via getRawButtonPressed()
   */
  private boolean[] m_buttonPressed, m_buttonReleased;

  /* Device number given to the constructor */
  private int m_deviceNum;

  /* Rumble and LED output last set states */
  private int m_outputs;
  @SuppressWarnings("unused")
  private short m_leftRumble;
  @SuppressWarnings("unused")
  private short m_rightRumble;

  /**
   * Create an object for a generic Human Interface Device (HID) plugged into the
   * RoboRIO
   * 
   * @param deviceNum Device number to open, filling in /dev/input/event#
   */
  public RIOGenericHID(int deviceNum) {
    // GenericHID's constructor only stores the given port number, so it's safe to
    // call with zero and completely ignore the underlying object
    super(0);

    m_deviceNum = deviceNum;
    setupDevice("/dev/input/event" + deviceNum);
  }

  /*
   * Handle one-time set up steps for opening an EventDevice by pathname
   */
  private void setupDevice(String devicePath) {
    try {
      m_device = new EventDevice(devicePath);
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }

    Map<Integer, List<Integer>> supportedEvents = m_device.getSupportedEvents();
    List<Integer> supportedAxes = supportedEvents.get((int) InputEvent.EV_ABS);
    List<Integer> supportedKeys = supportedEvents.get((int) InputEvent.EV_KEY);

    m_axesCount = supportedAxes == null ? 0 : supportedAxes.size();
    m_buttonsCount = supportedKeys == null ? 0 : supportedKeys.size();

    m_buttonEventCodes.addAll(supportedKeys);
    m_axisEventCodes.addAll(supportedAxes);

    System.out.println("Detected " + m_buttonEventCodes.size() + " buttons and " + m_axisEventCodes.size() + " axes.");

    m_axisStates = new int[m_axesCount];
    m_buttonStates = new boolean[m_buttonsCount];
    m_buttonPressed = new boolean[m_buttonsCount];
    m_buttonReleased = new boolean[m_buttonsCount];

    /*
     * Initialize the starting values of all axis to the midpoint between max and
     * min possible values. Buttons are initialized to the unpressed state.
     */
    for (int i = 0; i < m_axesCount; i++) {
      int axisCode = m_axisEventCodes.get(i);
      m_axisStates[i] = (m_device.getAxisParameters(axisCode).getMax() - m_device.getAxisParameters(axisCode).getMin()) / 2;
    }
    for (int i = 0; i < m_buttonsCount; i++)
      m_buttonStates[i] = m_buttonPressed[i] = m_buttonReleased[i] = false;

    /* Call the event() method (below) each time an input change is detected */
    m_device.addListener(this);
  }

  @Override
  public void event(InputEvent e) {
    /*
     * If all events are null, the EvDev library was probably compiled with the
     * wrong STRUCT_SIZE_BYTES for this architecture.
     */
    if (e != null) {
      switch (e.type) {
        case InputEvent.EV_KEY:
          handleButton(e.code, e.value > 0);
          break;
        case InputEvent.EV_ABS:
          handleAxis(e.code, e.value);
          break;
        case InputEvent.EV_SYN:
          /*
           * Indicates a set of button/axis changes detected in one pass by the driver has
           * now been completely delivered as a series of button and/or axis events. We
           * have no cleanup work or notifications to do, so just ignore them.
           */
        default:
          /* Unknown to us, ignore */
      }
    }
  }

  private void handleAxis(short axisNumber, int value) {
    int axisIndex = m_axisEventCodes.indexOf((int) axisNumber);
    if (axisIndex < 0) {
      System.err.println(
          "WARN: Couldn't find axis " + axisNumber + " in mapping! Perhaps device reported capabilities improperly!");
      return;
    }
    m_axisStates[axisIndex] = value;
  }

  private void handleButton(short buttonNumber, boolean buttonState) {
    int buttonIndex = m_buttonEventCodes.indexOf((int) buttonNumber);
    if (buttonIndex < 0) {
      System.err.println("WARN: Couldn't find button " + buttonNumber
          + " in mapping! Perhaps device reported capabilities improperly!");
      return;
    }
    /* Record as a button pressed if it was up and is now down */
    m_buttonPressed[buttonIndex] = (!m_buttonStates[buttonIndex] && buttonState);
    /* A button was released if it was down and is now up */
    m_buttonReleased[buttonIndex] = (m_buttonStates[buttonIndex] && !buttonState);

    m_buttonStates[buttonIndex] = buttonState;
  }

  /**
   * Get the x position of HID.
   *
   * @param hand which hand, left or right
   * @return the x position
   */
  public abstract double getX(Hand hand);

  /**
   * Get the y position of the HID.
   *
   * @param hand which hand, left or right
   * @return the y position
   */
  public abstract double getY(Hand hand);

  /**
   * Get the button value (starting at button 1).
   *
   * <p>
   * This method returns true if the button is being held down at the time that
   * this method is being called.
   *
   * @param button The button number to be read (starting at 1)
   * @return The state of the button.
   */
  public boolean getRawButton(int button) {
    return m_buttonStates[button - 1];
  }

  /**
   * Whether the button was pressed since the last check. Button indexes begin at
   * 1.
   *
   * <p>
   * This method returns true if the button went from not pressed to held down
   * since the last time this method was called. This is useful if you only want
   * to call a function once when you press the button.
   *
   * @param button The button index, beginning at 1.
   * @return Whether the button was pressed since the last check.
   */
  public boolean getRawButtonPressed(int button) {
    boolean retval;
    if (m_buttonPressed[button - 1]) {
      /*
       * Clear the button pressed state since we're about to tell the robot code it
       * happened
       */
      m_buttonPressed[button - 1] = false;
      retval = true;
    } else {
      retval = false;
    }
    return retval;
  }

  /**
   * Whether the button was released since the last check. Button indexes begin at
   * 1.
   *
   * <p>
   * This method returns true if the button went from held down to not pressed
   * since the last time this method was called. This is useful if you only want
   * to call a function once when you release the button.
   *
   * @param button The button index, beginning at 1.
   * @return Whether the button was released since the last check.
   */
  public boolean getRawButtonReleased(int button) {
    boolean retval;
    if (m_buttonReleased[button - 1]) {
      /*
       * Clear the button pressed state since we're about to tell the robot code it
       * happened
       */
      m_buttonReleased[button - 1] = false;
      retval = true;
    } else {
      retval = false;
    }
    return retval;
  }

  /**
   * Get the value of the axis.
   *
   * @param axis The axis to read, starting at 0.
   * @return The value of the axis.
   */
  public double getRawAxis(int axis) {
    return m_axisStates[axis];
  }

  /**
   * Get the angle in degrees of a POV on the HID.
   *
   * <p>
   * The POV angles start at 0 in the up direction, and increase clockwise (eg
   * right is 90, upper-left is 315).
   *
   * @param pov The index of the POV to read (starting at 0)
   * @return the angle of the POV in degrees, or -1 if the POV is not pressed.
   */
  public int getPOV(int pov) {
    return -1;
  }

  public int getPOV() {
    return getPOV(0);
  }

  /**
   * Get the number of axes for the HID.
   *
   * @return the number of axis for the current HID
   */
  public int getAxisCount() {
    return m_axesCount;
  }

  /**
   * For the current HID, return the number of POVs.
   */
  public int getPOVCount() {
    return 0;
  }

  /**
   * For the current HID, return the number of buttons.
   */
  public int getButtonCount() {
    return m_buttonsCount;
  }

  /**
   * Get the type of the HID.
   *
   * @return the type of the HID.
   */
  public HIDType getType() {
    return HIDType.kHIDGamepad;
  }

  /**
   * Get the name of the HID.
   *
   * @return the name of the HID.
   */
  public String getName() {
    return m_device.getDeviceName();
  }

  /**
   * Get the axis type of a joystick axis.
   *
   * @return the axis type of a joystick axis.
   */
  public int getAxisType(int axis) {
    return 7; // m_ds.getJoystickAxisType(m_port, axis);
  }

  /**
   * Get the port number of the HID.
   *
   * @return The port number of the HID.
   */
  public int getPort() {
    return m_deviceNum;
  }

  /**
   * Set a single HID output value for the HID.
   *
   * @param outputNumber The index of the output to set (1-32)
   * @param value        The value to set the output to
   */
  public void setOutput(int outputNumber, boolean value) {
    m_outputs = (m_outputs & ~(1 << (outputNumber - 1))) | ((value ? 1 : 0) << (outputNumber - 1));
    // HAL.setJoystickOutputs((byte) m_port, m_outputs, m_leftRumble,
    // m_rightRumble);
  }

  /**
   * Set all HID output values for the HID.
   *
   * @param value The 32 bit output value (1 bit for each output)
   */
  public void setOutputs(int value) {
    m_outputs = value;
    // HAL.setJoystickOutputs((byte) m_port, m_outputs, m_leftRumble,
    // m_rightRumble);
  }

  /**
   * Set the rumble output for the HID. The DS currently supports 2 rumble values,
   * left rumble and right rumble.
   *
   * @param type  Which rumble value to set
   * @param value The normalized value (0 to 1) to set the rumble to
   */
  public void setRumble(RumbleType type, double value) {
    if (value < 0) {
      value = 0;
    } else if (value > 1) {
      value = 1;
    }
    if (type == RumbleType.kLeftRumble) {
      m_leftRumble = (short) (value * 65535);
    } else {
      m_rightRumble = (short) (value * 65535);
    }
    // HAL.setJoystickOutputs((byte) m_port, m_outputs, m_leftRumble,
    // m_rightRumble);
  }
}
