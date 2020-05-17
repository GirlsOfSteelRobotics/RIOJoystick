/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import frc.robot.subsystems.Motor;
import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 * An example command that uses an example subsystem.
 */
public class MotorStart extends CommandBase {
  private final Motor m_motor;
  private double m_speed;
  private boolean m_finishImmediately;

  /**
   * Creates a new ExampleCommand.
   *
   * @param motor The subsystem used by this command.
   */
  public MotorStart(Motor motor, double speed, boolean finishImmediately) {
    m_motor = motor;
    m_speed = speed;
    m_finishImmediately = finishImmediately;

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(motor);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    System.out.printf("MotorStart(speed=%f,finishImmediately=%s) initialized\n", m_speed, m_finishImmediately);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_motor.spinMotor(m_speed);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    System.out.println("MotorStart ended");
    m_motor.stopMotor();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_finishImmediately;
  }
}
