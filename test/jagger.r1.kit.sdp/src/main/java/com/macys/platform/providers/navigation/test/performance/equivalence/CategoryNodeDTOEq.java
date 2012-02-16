/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.macys.platform.providers.navigation.test.performance.equivalence;


import com.google.common.base.Equivalence;
import com.macys.platform.services.navigation.dto.CategoryNodeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoryNodeDTOEq {
    private final static Logger log = LoggerFactory.getLogger(CategoryNodeDTOEq.class);

    public static Equivalence<CategoryNodeDTO> categoryNodeIDsEquivalent(){
        return new Equivalence<CategoryNodeDTO>(){
            @Override
            public boolean equivalent(CategoryNodeDTO first, CategoryNodeDTO second) {
                boolean result = false;
                try {
                    result = first.getCategoryNode().getId().equals(second.getCategoryNode().getId());
                } catch (Exception e) {
                    log.error("Error in CategoryNodeDTOEq.categoryNodeIDsEquivalent()", e);
                }
                return result;
            }

            @Override
            public int hash(CategoryNodeDTO categoryNodeDTO) {
                return 0;
            }
        };
    }
}
