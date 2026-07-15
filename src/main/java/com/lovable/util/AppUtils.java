package com.lovable.util;

import com.lovable.UnAuthorisedException;
import org.springframework.security.core.context.SecurityContextHolder;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.util
 * Created by: Ashish Kushwaha on 11-07-2026 18:32
 * File: AppUtils
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */
public class AppUtils {

    public static String getUserEmail() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        throw new UnAuthorisedException("User is not authenticated");
    }
}
