package com.seungma.daglo.presenter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.seungma.daglo.R
import com.seungma.daglo.databinding.ActivityMainBinding
import com.seungma.daglo.presenter.list.fragment.CharacterListFragment

sealed class EndPoint {
    object CharacterList : EndPoint()
    object Error : EndPoint()
}

interface Navigable {
    fun navigateFragment(endPoint: EndPoint)
}

class MainActivity() : AppCompatActivity(), Navigable {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigateFragment(endPoint = EndPoint.CharacterList)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setFragment(fragment: Fragment, viewId: Int, backStackToken: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        when (backStackToken) {
            true -> {
                transaction.replace(viewId, fragment)
                    .addToBackStack(null)
                    .commit()
            }

            false -> {
                transaction.replace(viewId, fragment)
                    .commit()
            }
        }

    }

    override fun navigateFragment(endPoint: EndPoint) {
        when (endPoint) {
            is EndPoint.CharacterList -> {
                val fragment = CharacterListFragment()
                setFragment(fragment, R.id.activity_frame_layout, false)
            }

            is EndPoint.Error -> {

            }
        }
    }

}
