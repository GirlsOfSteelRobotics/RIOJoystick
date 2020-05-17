package frc.robot;

import java.io.IOException;

import com.dgis.input.evdev.devices.EvdevJoystickFilter;

import edu.wpi.first.wpilibj.GenericHID;

public class RIOGenericHID extends GenericHID {

  private EvdevJoystickFilter evdev;
  private int m_deviceNum;
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
    try {
      evdev = new EvdevJoystickFilter("/dev/input/event" + deviceNum);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get the x position of HID.
   *
   * @param hand which hand, left or right
   * @return the x position
   */
  public double getX(Hand hand) {
    return 0.5;
  }

  /**
   * Get the y position of the HID.
   *
   * @param hand which hand, left or right
   * @return the y position
   */
  public double getY(Hand hand) {
    return -0.25;
  }

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
    return true;
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
    return true; // m_ds.getStickButtonPressed(m_port, (byte) button);
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
    return true; // m_ds.getStickButtonReleased(m_port, button);
  }

  /**
   * Get the value of the axis.
   *
   * @param axis The axis to read, starting at 0.
   * @return The value of the axis.
   */
  public double getRawAxis(int axis) {
    return 100 + axis; // m_ds.getStickAxis(m_port, axis);
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
    return 90; // m_ds.getStickPOV(m_port, pov);
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
    return 6; // m_ds.getStickAxisCount(m_port);
  }

  /**
   * For the current HID, return the number of POVs.
   */
  public int getPOVCount() {
    return 1; // m_ds.getStickPOVCount(m_port);
  }

  /**
   * For the current HID, return the number of buttons.
   */
  public int getButtonCount() {
    return 12; // m_ds.getStickButtonCount(m_port);
  }

  /**
   * Get the type of the HID.
   *
   * @return the type of the HID.
   */
  public HIDType getType() {
    return HIDType.of(1); // m_ds.getJoystickType(m_port)
  }

  /**
   * Get the name of the HID.
   *
   * @return the name of the HID.
   */
  public String getName() {
    return "HID Name Goes Here"; // m_ds.getJoystickName(m_port);
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
