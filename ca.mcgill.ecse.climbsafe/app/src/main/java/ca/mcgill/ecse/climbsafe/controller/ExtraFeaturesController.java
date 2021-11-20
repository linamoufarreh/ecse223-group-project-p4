package ca.mcgill.ecse.climbsafe.controller;

import ca.mcgill.ecse.climbsafe.application.*;
import ca.mcgill.ecse.climbsafe.model.*;
import ca.mcgill.ecse.climbsafe.model.ClimbingPath.Difficulty;
import ca.mcgill.ecse.climbsafe.model.Review.Rating;
import ca.mcgill.ecse.climbsafe.persistence.ClimbSafePersistence;

public class ExtraFeaturesController {

  public static void rateClimb(String memberEmail, String rating, String comment)
      throws InvalidInputException {

    Member member = (Member) checkMemberExists(memberEmail);

    member.setReview(new Review(stringToRating(rating), comment, member, member.getAssignment(),
        ClimbSafeApplication.getClimbSafe()));

    ClimbSafePersistence.save(ClimbSafeApplication.getClimbSafe());
  }

  public static void setClimbingPath(String memberEmail, String location)
      throws InvalidInputException {

    Member member = (Member) checkMemberExists(memberEmail);

    ClimbingPath climbingPath = checkClimbingPathExists(location);

    member.getAssignment().setClimbingPath(climbingPath);

    ClimbSafePersistence.save(ClimbSafeApplication.getClimbSafe());
  }

  public static void addClimbingPath(String location, int length, String difficulty)
      throws InvalidInputException {

    // Checking for invalid location input
    if (location.trim().equals("")) {
      throw new InvalidInputException("The location must not be empty");
    }

    // Checks for invalid length input
    if (length <= 0) {
      throw new InvalidInputException("The length must be greater than 0");
    }

    // Checks to see if path with same name already exists in the system and outputs an error
    // if that is the case
    ClimbingPath existingPath = ClimbingPath.getWithLocation(location);

    if (existingPath != null) {

      throw new InvalidInputException("The location already exists");

    }
    // Putting the system we're working on in a local variable
    ClimbSafe system = ClimbSafeApplication.getClimbSafe();

    // Add the climbing path to the system
    system.addClimbingPath(
        new ClimbingPath(location, length, stringToDifficulty(difficulty), system));

    ClimbSafePersistence.save(system);
  }

  public static void updateClimbingPath(String oldLocation, String newLocation, int newLength,
      String newDifficulty) throws InvalidInputException {

    ClimbingPath climbingPathToChange = checkClimbingPathExists(oldLocation);

    ClimbingPath newClimbingPath = ClimbingPath.getWithLocation(newLocation);

    if (newClimbingPath != null)
      throw new InvalidInputException("A location with the same name already exists");

    if (newLength <= 0)
      throw new InvalidInputException("The length must be greater than 0");

    if (!oldLocation.equals(newLocation))
      climbingPathToChange.setLocation(newLocation);

    climbingPathToChange.setLength(newLength);

    climbingPathToChange.setDifficulty(stringToDifficulty(newDifficulty));

    ClimbSafePersistence.save(ClimbSafeApplication.getClimbSafe());
  }

  public static void deleteClimbingPath(String location) throws InvalidInputException {

    ClimbingPath path = ClimbingPath.getWithLocation(location);

    if (path == null) {
      throw new InvalidInputException("No Climbing Path exists at this location");
    } else {
      path.delete();
    }

    ClimbSafePersistence.save(ClimbSafeApplication.getClimbSafe());
  }


  private static Difficulty stringToDifficulty(String difficulty) throws InvalidInputException {

    switch (difficulty) {

      case ("Easy"):
        return Difficulty.Easy;

      case ("Moderate"):
        return Difficulty.Moderate;

      case ("Hard"):
        return Difficulty.Hard;

      default:
        throw new InvalidInputException("Invalid Rating: " + difficulty);
    }
  }

  private static Rating stringToRating(String rating) throws InvalidInputException {

    switch (rating) {

      case ("VeryPoor"):
        return Rating.VeryPoor;

      case ("Poor"):
        return Rating.Poor;

      case ("Neutral"):
        return Rating.Neutral;

      case ("Good"):
        return Rating.Good;
      case ("VeryGood"):
        return Rating.VeryGood;

      default:
        throw new InvalidInputException("Invalid Rating: " + rating);
    }
  }

  private static User checkMemberExists(String memberEmail) throws InvalidInputException {

    User member = User.getWithEmail(memberEmail);

    if (member == null || member instanceof Guide)
      throw new InvalidInputException(
          "Member with email address " + memberEmail + " does not exist");

    return member;
  }

  private static ClimbingPath checkClimbingPathExists(String location)
      throws InvalidInputException {

    ClimbingPath climbingPath = ClimbingPath.getWithLocation(location);

    if (climbingPath == null)
      throw new InvalidInputException(
          "Climbing path with location " + location + " does not exist");

    return climbingPath;
  }



}
