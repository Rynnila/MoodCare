package com.healthapp.emotional.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Este é um ViewModel temporário para satisfazer as dependências do Hilt.
 * Na prática, devemos usar o SessionsViewModel em vez disso.
 * Este ViewModel será removido no futuro.
 */
@HiltViewModel
class SessionViewModel @Inject constructor() : ViewModel() 