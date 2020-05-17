/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants;

public class Motor extends SubsystemBase {

  private final WPI_TalonSRX m_motor;

  /**
   * Creates a new Motor.
   */
  public Motor() {
    m_motor = new WPI_TalonSRX(Constants.MOTOR_TALON_ID);
  }

  public void spinMotor(final double speed) {
    m_motor.set(ControlMode.PercentOutput, speed);
  }

  public void stopMotor() {
    m_motor.stopMotor();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
