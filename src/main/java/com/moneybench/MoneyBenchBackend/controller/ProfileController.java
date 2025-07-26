package com.moneybench.MoneyBenchBackend.controller;

import com.moneybench.MoneyBenchBackend.dto.AuthDTO;
import com.moneybench.MoneyBenchBackend.dto.ProfileDTO;
import com.moneybench.MoneyBenchBackend.service.ProfileService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController
{

    private final ProfileService profileService;

    @PostMapping("/profile/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO)
    {
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    @GetMapping("/profile/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token)
    {
        boolean isActivated = profileService.activateProfile(token);
        if (isActivated)
        {
            return ResponseEntity.ok("Profile activation successful");
        }
        else
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Activation Token. Please try again later.");
        }
    }

    @PostMapping("/profile/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO)
    {
        try
        {
            if (!profileService.isAccountActive(authDTO.getEmail()))
            {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "Please activate your account first."
                ));
            }
            Map<String, Object> response = profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDTO> getPublicProfile()
    {
        ProfileDTO profileDTO = profileService.getPublicProfile(null);
        return ResponseEntity.ok(profileDTO);
    }
}
