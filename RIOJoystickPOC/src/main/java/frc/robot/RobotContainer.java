/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.commands.MotorStart;
import frc.robot.commands.MotorStop;
import frc.robot.subsystems.Motor;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final Motor m_motor = new Motor();

  // In autonomous mode, spin the motor slowly until disabled
  private final MotorStart m_autoCommand = new MotorStart(m_motor, 0.5, false);

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by instantiating a {@link GenericHID} or one of its subclasses
   * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
   * passing it to a {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    final Joystick joystick = new Joystick(Constants.JOYSTICK_PORT_NUMBER);

    new JoystickButton(joystick, 1).whileHeld(new MotorStart(m_motor, 0.75, false));
    new JoystickButton(joystick, 2).whenPressed(new MotorStart(m_motor, 0.25, false));
    new JoystickButton(joystick, 3).whenPressed(new MotorStop(m_motor));

    final RIOGenericHID gamepad = new RIOJoystick(Constants.GAMEPAD_DEVICE_NUMBER);
    new JoystickButton(gamepad, 1).whileHeld(new MotorStart(m_motor, 0.75, false));
    new JoystickButton(gamepad, 2).whenPressed(new MotorStart(m_motor, 0.25, false));
    new JoystickButton(gamepad, 3).whenPressed(new MotorStop(m_motor));

  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return m_autoCommand;
  }
}
